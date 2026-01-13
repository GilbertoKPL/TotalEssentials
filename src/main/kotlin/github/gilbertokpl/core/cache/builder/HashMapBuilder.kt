package github.gilbertokpl.core.cache.builder

import github.gilbertokpl.core.cache.interfaces.ICacheBuilderExtended
import github.gilbertokpl.core.cache.interfaces.ICacheLogger
import github.gilbertokpl.core.cache.interfaces.ICacheSerializer
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.LowerCase
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.concurrent.write
import kotlin.concurrent.read

/**
 * Builder para cache de HashMaps com persistência.
 * Suporta operações de adição/remoção de chaves individuais.
 *
 * IMPORTANTE: 
 * - NUNCA faz DELETE da linha (isso apagaria todas as colunas do jogador)
 * - Remove apenas limpa o HashMap
 * - INSERT é feito apenas pelo PlayerData.createNewPlayerData()
 *
 * @param K Tipo das chaves do HashMap
 * @param V Tipo dos valores do HashMap
 * @param D Tipo no banco de dados (geralmente String ou Int)
 */
class HashMapBuilder<K, V, D>(
    table: Table,
    primaryColumn: Column<String>,
    column: Column<D>,
    private val serializer: ICacheSerializer<HashMap<K, V>, D>,
    logger: ICacheLogger
) : AbstractCacheBuilder<HashMap<K, V>, D>(table, primaryColumn, column, logger),
    ICacheBuilderExtended<HashMap<K, V>, K> {

    override fun toDatabase(value: HashMap<K, V>): D = serializer.convertToDatabase(value)

    override fun fromDatabase(value: D): HashMap<K, V>? = serializer.convertToCache(value)

    override fun defaultValue(): HashMap<K, V> = HashMap()

    override fun shouldDelete(value: HashMap<K, V>?): Boolean = value == null

    // =========================================================
    // Operações de set
    // =========================================================

    override operator fun set(entity: String, value: HashMap<K, V>, override: Boolean) {
        val key = normalizeKey(entity)
        rwLock.write {
            if (override) {
                cache[key] = value
            } else {
                val existing = cache[key] ?: HashMap()
                existing.putAll(value)
                cache[key] = existing
            }
            markedForDeletion.remove(key)
            markForUpdate(key)
        }
    }

    // =========================================================
    // Operações estendidas (ICacheBuilderExtended)
    // =========================================================

    override fun add(entity: String, value: K) {
        throw UnsupportedOperationException(
            "Use put(entity, key, value) para adicionar entradas ao HashMap"
        )
    }

    override fun add(entity: Player, value: K) = add(entity.name, value)

    /**
     * Adiciona ou atualiza uma entrada no HashMap de uma entidade.
     */
    fun put(entity: String, mapKey: K, mapValue: V) {
        val key = normalizeKey(entity)
        rwLock.write {
            val map = cache[key] ?: HashMap()
            map[mapKey] = mapValue
            cache[key] = map
            markedForDeletion.remove(key)
            markForUpdate(key)
        }
    }

    /**
     * Adiciona ou atualiza uma entrada no HashMap de um Player.
     */
    fun put(entity: Player, mapKey: K, mapValue: V) = put(entity.name, mapKey, mapValue)

    /**
     * Obtém um valor específico do HashMap de uma entidade.
     */
    fun getValue(entity: String, mapKey: K): V? {
        rwLock.read {
            return cache[normalizeKey(entity)]?.get(mapKey)
        }
    }

    /**
     * Obtém um valor específico do HashMap de um Player.
     */
    fun getValue(entity: Player, mapKey: K): V? = getValue(entity.name, mapKey)

    override fun remove(entity: String, value: K) {
        val key = normalizeKey(entity)
        rwLock.write {
            val map = cache[key] ?: return
            map.remove(value)
            cache[key] = map
            markForUpdate(key)
        }
    }

    override fun remove(entity: Player, value: K) = remove(entity.name, value)

    override fun contains(entity: String, value: K): Boolean {
        rwLock.read {
            return cache[normalizeKey(entity)]?.containsKey(value) ?: false
        }
    }

    override fun contains(entity: Player, value: K): Boolean = contains(entity.name, value)

    /**
     * Verifica se o HashMap de uma entidade contém um valor.
     */
    fun containsValue(entity: String, value: V): Boolean {
        rwLock.read {
            return cache[normalizeKey(entity)]?.containsValue(value) ?: false
        }
    }

    // =========================================================
    // Persistência - NUNCA DELETA A LINHA!
    // =========================================================

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
                val value = cache[key] ?: HashMap() // Se null, usa HashMap vazio
                val originalKey = existingRows[key]
                val dbValue = toDatabase(value)

                try {
                    when {
                        // Existe no banco - faz UPDATE
                        originalKey != null -> {
                            logger.log("Atualizando entidade: $key, coluna: ${column.name}", 2)
                            table.update({ primaryColumn eq originalKey }) {
                                it[column] = dbValue
                            }
                        }
                        
                        // Não existe no banco - ignora (INSERT é responsabilidade do PlayerData)
                        else -> {
                            logger.warn("SKIP: Entidade não existe no banco: $key, coluna: ${column.name}")
                        }
                    }
                    pendingUpdates.remove(key)
                } catch (e: Exception) {
                    logger.error("Erro ao persistir hashmap: $key, coluna: ${column.name}", e)
                }
            }
        }
    }

    override fun load() {
        rwLock.write {
            table.selectAll().forEach { row ->
                val key = normalizeKey(row[primaryColumn])
                cache[key] = fromDatabase(row[column]) ?: HashMap()
            }
        }
        logger.log("Carregado ${cache.size} hashmaps para coluna: ${column.name}")
    }

    // =========================================================
    // Métodos utilitários
    // =========================================================

    /**
     * Retorna o tamanho do HashMap de uma entidade.
     */
    fun sizeOf(entity: String): Int {
        rwLock.read {
            return cache[normalizeKey(entity)]?.size ?: 0
        }
    }

    /**
     * Verifica se o HashMap de uma entidade está vazio.
     */
    fun isEmpty(entity: String): Boolean {
        rwLock.read {
            return cache[normalizeKey(entity)]?.isEmpty() ?: true
        }
    }

    /**
     * Retorna as chaves do HashMap de uma entidade.
     */
    fun keysOf(entity: String): Set<K> {
        rwLock.read {
            return cache[normalizeKey(entity)]?.keys?.toSet() ?: emptySet()
        }
    }

    /**
     * Retorna os valores do HashMap de uma entidade.
     */
    fun valuesOf(entity: String): Collection<V> {
        rwLock.read {
            return cache[normalizeKey(entity)]?.values?.toList() ?: emptyList()
        }
    }

    /**
     * Limpa o HashMap de uma entidade (sem deletar do banco).
     */
    fun clear(entity: String) {
        val key = normalizeKey(entity)
        rwLock.write {
            cache[key] = HashMap()
            markForUpdate(key)
        }
    }
}
