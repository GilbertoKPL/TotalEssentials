package github.gilbertokpl.core.cache.builder

import github.gilbertokpl.core.cache.interfaces.ICacheBuilder
import github.gilbertokpl.core.cache.interfaces.ICacheLogger
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * Classe base abstrata para builders de cache com persistência.
 *
 * Fornece:
 * - Normalização de chaves (lowercase)
 * - Thread-safety via ReadWriteLock
 * - Gerenciamento de pendências de update
 * - Estrutura comum para persistência
 *
 * @param T Tipo do valor no cache
 * @param D Tipo do valor no banco de dados
 */
abstract class AbstractCacheBuilder<T, D>(
    protected val table: Table,
    protected val primaryColumn: Column<String>,
    protected val column: Column<D>,
    protected val logger: ICacheLogger
) : ICacheBuilder<T> {

    // Cache em memória
    protected val cache = HashMap<String, T?>()

    // Lock para thread-safety
    protected val rwLock = ReentrantReadWriteLock()

    // Chaves com alterações pendentes para persistir
    protected val pendingUpdates = mutableSetOf<String>()

    // Chaves marcadas para deleção (limpar valor, não deletar linha)
    protected val markedForDeletion = mutableSetOf<String>()

    // =========================================================
    // Métodos abstratos para conversão
    // =========================================================

    /**
     * Converte valor do cache para formato do banco.
     */
    protected abstract fun toDatabase(value: T): D

    /**
     * Converte valor do banco para formato do cache.
     */
    protected abstract fun fromDatabase(value: D): T?

    // =========================================================
    // Métodos opcionais para override
    // =========================================================

    /**
     * Valor default para novas entradas.
     * Override para tipos com valor default específico.
     */
    protected open fun defaultValue(): T? = null

    /**
     * Determina se um valor deve ser tratado como "deletado".
     * Override para lógica customizada.
     */
    protected open fun shouldDelete(value: T?): Boolean = value == null

    // =========================================================
    // Normalização de chaves
    // =========================================================

    /**
     * Normaliza a chave para lowercase.
     * Garante consistência entre cache e banco.
     */
    protected fun normalizeKey(key: String): String = key.lowercase()

    /**
     * Marca uma chave para ser persistida no próximo update.
     */
    protected fun markForUpdate(key: String) {
        pendingUpdates.add(key)
    }

    // =========================================================
    // Implementação de ICacheBuilder - Leitura
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

    override fun hasPendingUpdates(): Boolean {
        rwLock.read {
            return pendingUpdates.isNotEmpty()
        }
    }

    // =========================================================
    // Implementação de ICacheBuilder - Escrita
    // =========================================================

    override operator fun set(entity: String, value: T) {
        set(entity, value, override = true)
    }

    override operator fun set(entity: Player, value: T) {
        set(entity.name, value)
    }

    /**
     * Implementação padrão de set com override.
     * Subclasses podem sobrescrever para comportamento específico.
     */
    override operator fun set(entity: String, value: T, override: Boolean) {
        val key = normalizeKey(entity)
        rwLock.write {
            cache[key] = value
            markedForDeletion.remove(key)
            markForUpdate(key)
        }
    }

    override fun remove(entity: String) {
        val key = normalizeKey(entity)
        rwLock.write {
            cache.remove(key)
            markedForDeletion.add(key)
            markForUpdate(key)
        }
    }

    override fun remove(entity: Player) = remove(entity.name)

    // =========================================================
    // Ciclo de vida - devem ser implementados pelas subclasses
    // =========================================================

    /**
     * Persiste alterações pendentes no banco.
     * Subclasses devem implementar a lógica de UPDATE.
     */
    abstract override fun update()

    /**
     * Carrega dados do banco para o cache.
     * Subclasses devem implementar a lógica de SELECT.
     */
    abstract override fun load()

    /**
     * Salva e limpa recursos.
     * Implementação padrão: update + clear.
     */
    override fun unload() {
        update()
        rwLock.write {
            cache.clear()
            pendingUpdates.clear()
            markedForDeletion.clear()
        }
    }
}
