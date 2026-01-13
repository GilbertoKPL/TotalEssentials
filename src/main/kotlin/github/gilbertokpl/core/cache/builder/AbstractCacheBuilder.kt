package github.gilbertokpl.core.cache.builder

import github.gilbertokpl.core.cache.interfaces.ICacheBuilder
import github.gilbertokpl.core.cache.interfaces.ICacheLogger
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * Classe base abstrata para todos os builders de cache persistentes.
 * Fornece implementação comum de thread-safety e operações básicas.
 *
 * IMPORTANTE - REGRAS DE PERSISTÊNCIA:
 * 1. NUNCA faz DELETE da linha (isso apagaria todas as colunas do jogador)
 * 2. Remove apenas limpa o valor para default (null, "", HashMap vazio, etc)
 * 3. INSERT é feito APENAS pelo PlayerData.createNewPlayerData()
 * 4. Builders só fazem UPDATE de registros existentes
 *
 * @param T Tipo do valor armazenado
 * @param D Tipo no banco de dados
 */
abstract class AbstractCacheBuilder<T, D>(
    protected val table: Table,
    protected val primaryColumn: Column<String>,
    protected val column: Column<D>,
    protected val logger: ICacheLogger
) : ICacheBuilder<T> {

    // HashMap normal protegido por lock (ConcurrentHashMap não aceita null)
    protected val cache = HashMap<String, T?>()

    // Set de chaves pendentes para atualização
    protected val pendingUpdates = ConcurrentHashMap.newKeySet<String>()

    // REMOVIDO: markedForDeletion - não usamos mais DELETE
    // Mantido apenas para compatibilidade, mas não é usado
    protected val markedForDeletion = ConcurrentHashMap.newKeySet<String>()

    // ReadWriteLock para operações no cache
    protected val rwLock = ReentrantReadWriteLock()

    // =========================================================
    // Métodos utilitários
    // =========================================================

    /**
     * Normaliza a chave para lowercase.
     */
    protected fun normalizeKey(key: String): String = key.lowercase()

    /**
     * Marca uma chave para atualização.
     */
    protected fun markForUpdate(key: String) {
        pendingUpdates.add(normalizeKey(key))
    }

    // =========================================================
    // Implementações de ICacheBuilder
    // =========================================================

    override fun getMap(): Map<String, T?> {
        rwLock.read {
            return cache.mapValues { (_, value) ->
                when (value) {
                    is HashMap<*, *> -> HashMap(value) as T   // cópia profunda
                    else -> value
                }
            }
        }
    }

    override operator fun get(entity: String): T? {
        val key = normalizeKey(entity)
        rwLock.read {
            val value = cache[key] ?: return null

            return when (value) {
                is HashMap<*, *> -> HashMap(value) as T       // nunca retornar o original
                else -> value
            }
        }
    }

    override operator fun get(entity: Player): T? = get(entity.name.lowercase())

    override operator fun set(entity: Player, value: T) = set(entity.name.lowercase(), value)

    override operator fun set(entity: String, value: T) = set(entity.lowercase(), value, override = true)

    override fun remove(entity: Player) = remove(entity.name.lowercase())

    override fun contains(entity: String): Boolean {
        val key = normalizeKey(entity.lowercase())
        rwLock.read {
            return cache.containsKey(key) && cache[key] != null
        }
    }

    override fun contains(entity: Player): Boolean = contains(entity.name.lowercase())

    override fun size(): Int {
        rwLock.read {
            return cache.count { it.value != null }
        }
    }

    override fun hasPendingUpdates(): Boolean = pendingUpdates.isNotEmpty()

    /**
     * Remove apenas limpa o valor no cache.
     * NÃO marca para deleção no banco - apenas atualiza para valor default.
     */
    override fun remove(entity: String) {
        val key = normalizeKey(entity.lowercase())
        rwLock.write {
            cache[key] = null
            // NÃO adiciona em markedForDeletion
            // Apenas marca para update (vai salvar como valor vazio/default)
            markForUpdate(key)
        }
    }

    override fun unload() {
        update()
    }

    // =========================================================
    // Métodos auxiliares (mantidos para compatibilidade)
    // =========================================================

    /**
     * @deprecated Não usamos mais deleção de linhas
     */
    protected fun isMarkedForDeletion(key: String): Boolean {
        return false // Sempre retorna false - não deletamos mais
    }

    /**
     * @deprecated Não usamos mais deleção de linhas
     */
    protected fun clearDeletionMark(key: String) {
        markedForDeletion.remove(key)
    }

    // =========================================================
    // Métodos abstratos para implementação específica
    // =========================================================

    /**
     * Converte o valor para o tipo do banco de dados.
     */
    protected abstract fun toDatabase(value: T): D

    /**
     * Converte o valor do banco de dados para o tipo em memória.
     */
    protected abstract fun fromDatabase(value: D): T?

    /**
     * Retorna o valor padrão para novas entradas (se aplicável).
     */
    protected open fun defaultValue(): T? = null

    /**
     * Verifica se o valor deve ser tratado como "vazio".
     * NÃO é mais usado para deleção.
     */
    protected open fun shouldDelete(value: T?): Boolean = value == null
}
