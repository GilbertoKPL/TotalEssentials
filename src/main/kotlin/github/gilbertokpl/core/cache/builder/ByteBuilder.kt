package github.gilbertokpl.core.cache.builder

import github.gilbertokpl.core.cache.interfaces.ICacheLogger
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.LowerCase
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.concurrent.write

/**
 * Builder para tipos primitivos e simples (String, Boolean, Int, Double, Long).
 * Persiste valores diretamente na coluna sem conversão.
 * 
 * IMPORTANTE: 
 * - NUNCA faz DELETE da linha (isso apagaria todas as colunas do jogador)
 * - Remove apenas limpa o valor para o default
 * - INSERT é feito apenas pelo PlayerData.createNewPlayerData()
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

            // Busca quais registros existem no banco (com chave original)
            val existingRows = table.selectAll()
                .where { LowerCase(primaryColumn) inList keysToProcess }
                .associate { it[primaryColumn].lowercase() to it[primaryColumn] }

            for (key in keysToProcess) {
                val value = cache[key]
                val originalKey = existingRows[key]

                try {
                    when {
                        // Existe no banco - faz UPDATE (mesmo se value for null, apenas limpa)
                        originalKey != null -> {
                            if (value != null) {
                                logger.log("Atualizando entidade: $key, coluna: ${column.name}, valor: $value", 2)
                                table.update({ primaryColumn eq originalKey }) {
                                    it[column] = value
                                }
                            }
                            // Se value for null, não faz nada - não podemos "limpar" um tipo primitivo
                            // O valor permanece o que está no banco
                        }
                        
                        // Não existe no banco - ignora (INSERT é responsabilidade do PlayerData)
                        value != null -> {
                            logger.warn("SKIP: Entidade não existe no banco: $key, coluna: ${column.name}")
                        }
                    }
                    pendingUpdates.remove(key)
                } catch (e: Exception) {
                    logger.error("Erro ao persistir entidade: $key, coluna: ${column.name}", e)
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
