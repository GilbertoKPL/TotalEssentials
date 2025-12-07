package github.gilbertokpl.core.cache.interfaces

import org.bukkit.entity.Player

/**
 * Interface estendida para builders que armazenam coleções.
 * Adiciona operações para manipular elementos individuais dentro da coleção.
 *
 * @param T Tipo da coleção (ex: ArrayList, HashMap)
 * @param V Tipo do elemento/chave a ser removido individualmente
 */
interface ICacheBuilderExtended<T, V> : ICacheBuilder<T> {

    /**
     * Remove um elemento específico da coleção de uma entidade.
     */
    fun remove(entity: String, value: V)

    /**
     * Remove um elemento específico da coleção de um Player.
     */
    fun remove(entity: Player, value: V)

    /**
     * Adiciona um único elemento à coleção de uma entidade.
     */
    fun add(entity: String, value: V)

    /**
     * Adiciona um único elemento à coleção de um Player.
     */
    fun add(entity: Player, value: V)

    /**
     * Verifica se a coleção de uma entidade contém um elemento.
     */
    fun contains(entity: String, value: V): Boolean

    /**
     * Verifica se a coleção de um Player contém um elemento.
     */
    fun contains(entity: Player, value: V): Boolean
}
