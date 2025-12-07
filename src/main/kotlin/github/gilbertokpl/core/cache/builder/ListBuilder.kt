package github.gilbertokpl.core.cache.builder

import github.gilbertokpl.core.cache.interfaces.ICacheBuilderExtended
import github.gilbertokpl.core.cache.interfaces.ICacheLogger
import github.gilbertokpl.core.cache.interfaces.ICacheSerializer
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.concurrent.write
import kotlin.concurrent.read

/**
 * Builder para cache de listas com persistência.
 * Suporta operações de adição/remoção de elementos individuais.
 *
 * @param V Tipo dos elementos da lista
 * @param D Tipo no banco de dados (geralmente String)
 */
internal class ListBuilder<V, D>(
    table: Table,
    primaryColumn: Column<String>,
    column: Column<D>,
    private val serializer: ICacheSerializer<ArrayList<V>, D>,
    logger: ICacheLogger
) : AbstractCacheBuilder<ArrayList<V>, D>(table, primaryColumn, column, logger),
    ICacheBuilderExtended<ArrayList<V>, V> {

    override fun toDatabase(value: ArrayList<V>): D = serializer.convertToDatabase(value)

    override fun fromDatabase(value: D): ArrayList<V>? = serializer.convertToCache(value)

    override fun defaultValue(): ArrayList<V> = ArrayList()

    override fun shouldDelete(value: ArrayList<V>?): Boolean = value == null

    // =========================================================
    // Operações de set
    // =========================================================

    override operator fun set(entity: String, value: ArrayList<V>, override: Boolean) {
        val key = normalizeKey(entity)
        rwLock.write {
            if (override) {
                cache[key] = value
            } else {
                val existing = cache[key] ?: ArrayList()
                existing.addAll(value)
                cache[key] = existing
            }
            markedForDeletion.remove(key)
            markForUpdate(key)
        }
    }

    // =========================================================
    // Operações estendidas (ICacheBuilderExtended)
    // =========================================================

    override fun add(entity: String, value: V) {
        val key = normalizeKey(entity)
        rwLock.write {
            val list = cache[key] ?: ArrayList()
            list.add(value)
            cache[key] = list
            markedForDeletion.remove(key)
            markForUpdate(key)
        }
    }

    override fun add(entity: Player, value: V) = add(entity.name, value)

    override fun remove(entity: String, value: V) {
        val key = normalizeKey(entity)
        rwLock.write {
            val list = cache[key] ?: return
            list.remove(value)
            cache[key] = list
            markForUpdate(key)
        }
    }

    override fun remove(entity: Player, value: V) = remove(entity.name, value)

    override fun contains(entity: String, value: V): Boolean {
        rwLock.read {
            return cache[normalizeKey(entity)]?.contains(value) ?: false
        }
    }

    override fun contains(entity: Player, value: V): Boolean = contains(entity.name, value)

    // =========================================================
    // Persistência
    // =========================================================

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
                        // Deletar se null e existe no banco
                        shouldRemove && originalKey != null -> {
                            logger.log("Removendo entidade: $key, coluna: ${column.name}")
                            table.deleteWhere { primaryColumn eq originalKey }
                            clearDeletionMark(key)
                        }
                        // Inserir se não existe
                        value != null && originalKey == null -> {
                            val dbValue = toDatabase(value)
                            logger.log("Inserindo entidade: $key, coluna: ${column.name}")
                            table.insert {
                                it[primaryColumn] = key
                                it[column] = dbValue
                            }
                        }
                        // Atualizar se existe
                        value != null && originalKey != null -> {
                            val dbValue = toDatabase(value)
                            logger.log("Atualizando entidade: $key, coluna: ${column.name}")
                            table.update({ primaryColumn eq originalKey }) {
                                it[column] = dbValue
                            }
                        }
                    }
                    pendingUpdates.remove(key)
                } catch (e: Exception) {
                    logger.error("Erro ao persistir lista: $key, coluna: ${column.name}", e)
                }
            }
        }
    }

    override fun load() {
        rwLock.write {
            table.selectAll().forEach { row ->
                val key = normalizeKey(row[primaryColumn])
                cache[key] = fromDatabase(row[column]) ?: ArrayList()
            }
        }
        logger.log("Carregado ${cache.size} listas para coluna: ${column.name}")
    }

    // =========================================================
    // Métodos utilitários
    // =========================================================

    /**
     * Retorna o tamanho da lista de uma entidade.
     */
    fun sizeOf(entity: String): Int {
        rwLock.read {
            return cache[normalizeKey(entity)]?.size ?: 0
        }
    }

    /**
     * Verifica se a lista de uma entidade está vazia.
     */
    fun isEmpty(entity: String): Boolean {
        rwLock.read {
            return cache[normalizeKey(entity)]?.isEmpty() ?: true
        }
    }

    /**
     * Limpa a lista de uma entidade (sem deletar do banco).
     */
    fun clear(entity: String) {
        val key = normalizeKey(entity)
        rwLock.write {
            cache[key]?.clear()
            markForUpdate(key)
        }
    }
}
