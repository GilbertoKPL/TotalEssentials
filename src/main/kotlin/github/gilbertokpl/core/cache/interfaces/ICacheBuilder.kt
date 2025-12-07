package github.gilbertokpl.core.cache.interfaces

import org.bukkit.entity.Player

/**
 * Interface principal para builders de cache.
 * Define operações básicas de CRUD em memória com persistência.
 */
interface ICacheBuilder<T> {

    /**
     * Persiste alterações pendentes no banco de dados.
     */
    fun update()

    /**
     * Carrega dados do banco de dados para a memória.
     */
    fun load()

    /**
     * Persiste e limpa recursos. Chamado no shutdown.
     */
    fun unload()

    /**
     * Retorna uma cópia imutável do mapa interno.
     */
    fun getMap(): Map<String, T?>

    /**
     * Obtém valor por nome da entidade.
     */
    operator fun get(entity: String): T?

    /**
     * Obtém valor pelo Player.
     */
    operator fun get(entity: Player): T?

    /**
     * Define valor para uma entidade.
     */
    operator fun set(entity: String, value: T)

    /**
     * Define valor para uma entidade com opção de override.
     * @param override Se true, substitui completamente. Se false, pode mesclar (para coleções).
     */
    operator fun set(entity: String, value: T, override: Boolean)

    /**
     * Define valor para um Player.
     */
    operator fun set(entity: Player, value: T)

    /**
     * Remove entidade do cache (marca para deleção no banco).
     */
    fun remove(entity: String)

    /**
     * Remove Player do cache.
     */
    fun remove(entity: Player)

    /**
     * Verifica se a entidade existe no cache.
     */
    fun contains(entity: String): Boolean

    /**
     * Verifica se o Player existe no cache.
     */
    fun contains(entity: Player): Boolean

    /**
     * Retorna quantidade de entradas no cache.
     */
    fun size(): Int

    /**
     * Verifica se há alterações pendentes.
     */
    fun hasPendingUpdates(): Boolean
}
