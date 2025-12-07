package github.gilbertokpl.core.cache.builder

import github.gilbertokpl.core.cache.interfaces.ICacheLogger
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.concurrent.write

/**
 * Builder para tipos primitivos e simples (String, Boolean, Int, Double, Long).
 * Persiste valores diretamente na coluna sem conversão.
 */
internal class ByteBuilder<T>(
    table: Table,
    primaryColumn: Column<String>,
    column: Column<T>,
    logger: ICacheLogger
) : AbstractCacheBuilder<T, T>(table, primaryColumn, column, logger) {

    override fun toDatabase(value: T): T = value

    override fun fromDatabase(value: T): T? = value

    override operator fun set(entity: String, value: T, override: Boolean) {
        val key = normalizeKey(entity)
        rwLock.write {
            cache[key] = value
            markedForDeletion.remove(key)
            markForUpdate(key)
        }
    }

    override fun update() {
        if (pendingUpdates.isEmpty()) return

        rwLock.write {
            val keysToProcess = pendingUpdates.toList()
            if (keysToProcess.isEmpty()) return

            val existingRows = table.selectAll()
                .where { primaryColumn inList keysToProcess }
                .associate { it[primaryColumn].lowercase() to it[primaryColumn] }

            for (key in keysToProcess) {
                val value = cache[key]
                val originalKey = existingRows[key]
                val shouldRemove = isMarkedForDeletion(key) || value == null

                try {
                    when {
                        // Deletar se valor é null e existe no banco
                        shouldRemove && originalKey != null -> {
                            logger.log("Removendo entidade: $key, coluna: ${column.name}")
                            table.deleteWhere { primaryColumn eq originalKey }
                            clearDeletionMark(key)
                        }
                        // Inserir se não existe no banco
                        value != null && originalKey == null -> {
                            logger.log("Inserindo entidade: $key, coluna: ${column.name}, valor: $value")
                            table.insert {
                                it[primaryColumn] = key
                                it[column] = value
                            }
                        }
                        // Atualizar se existe no banco
                        value != null && originalKey != null -> {
                            logger.log("Atualizando entidade: $key, coluna: ${column.name}, valor: $value")
                            table.update({ primaryColumn eq originalKey }) {
                                it[column] = value
                            }
                        }
                    }
                    pendingUpdates.remove(key)
                } catch (e: Exception) {
                    logger.error("Erro ao persistir entidade: $key, coluna: ${column.name}", e)
                    // Mantém na lista de pendentes para retry
                }
            }
        }
    }

    override fun load() {
        rwLock.write {
            table.selectAll().forEach { row ->
                val key = normalizeKey(row[primaryColumn])
                cache[key] = row[column]
            }
        }
        logger.log("Carregado ${cache.size} entradas para coluna: ${column.name}")
    }
}
