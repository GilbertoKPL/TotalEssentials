package github.gilbertokpl.core.cache.builder

import github.gilbertokpl.core.cache.interfaces.ICacheBuilder
import org.bukkit.entity.Player
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * Builder simples para cache em memória sem persistência.
 * Útil para dados temporários que não precisam ser salvos no banco.
 *
 * Thread-safe usando ReadWriteLock (HashMap não aceita null em algumas operações).
 */
class SimpleBuilder<T> : ICacheBuilder<T> {

    private val cache = HashMap<String, T?>()
    private val rwLock = ReentrantReadWriteLock()

    private fun normalizeKey(key: String): String = key.lowercase()

    // =========================================================
    // Operações no-op (sem persistência)
    // =========================================================

    override fun update() {
        // No-op: SimpleBuilder não persiste dados
    }

    override fun load() {
        // No-op: SimpleBuilder não carrega dados
    }

    override fun unload() {
        rwLock.write {
            cache.clear()
        }
    }

    // =========================================================
    // Operações de leitura
    // =========================================================

    override fun getMap(): Map<String, T?> {
        rwLock.read {
            return cache.toMap()
        }
    }

    override operator fun get(entity: String): T? {
        rwLock.read {
            return cache[normalizeKey(entity)]
        }
    }

    override operator fun get(entity: Player): T? = get(entity.name)

    override fun contains(entity: String): Boolean {
        rwLock.read {
            return cache.containsKey(normalizeKey(entity))
        }
    }

    override fun contains(entity: Player): Boolean = contains(entity.name)

    override fun size(): Int {
        rwLock.read {
            return cache.size
        }
    }

    override fun hasPendingUpdates(): Boolean = false // Nunca tem pendências

    // =========================================================
    // Operações de escrita
    // =========================================================

    override operator fun set(entity: String, value: T) {
        rwLock.write {
            cache[normalizeKey(entity)] = value
        }
    }

    override operator fun set(entity: String, value: T, override: Boolean) {
        set(entity, value)
    }

    override operator fun set(entity: Player, value: T) {
        set(entity.name, value)
    }

    override fun remove(entity: String) {
        rwLock.write {
            cache.remove(normalizeKey(entity))
        }
    }

    override fun remove(entity: Player) {
        remove(entity.name)
    }

    // =========================================================
    // Métodos utilitários adicionais
    // =========================================================

    /**
     * Limpa todo o cache.
     */
    fun clear() {
        rwLock.write {
            cache.clear()
        }
    }

    /**
     * Retorna todas as chaves.
     */
    fun keys(): Set<String> {
        rwLock.read {
            return cache.keys.toSet()
        }
    }

    /**
     * Retorna todos os valores não-nulos.
     */
    fun values(): Collection<T> {
        rwLock.read {
            return cache.values.filterNotNull()
        }
    }

    /**
     * Executa uma ação para cada entrada.
     */
    private inline fun forEach(action: (String, T?) -> Unit) {
        rwLock.read {
            cache.forEach { (k, v) -> action(k, v) }
        }
    }

    /**
     * Obtém ou computa um valor se ausente.
     */
    fun getOrPut(entity: String, defaultValue: () -> T): T {
        val key = normalizeKey(entity)
        rwLock.write {
            val existing = cache[key]
            if (existing != null) {
                return existing
            }
            val newValue = defaultValue()
            cache[key] = newValue
            return newValue
        }
    }
}
