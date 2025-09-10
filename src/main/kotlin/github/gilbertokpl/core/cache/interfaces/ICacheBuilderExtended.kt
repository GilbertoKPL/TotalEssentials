package github.gilbertokpl.core.cache.interfaces

import org.bukkit.entity.Player

interface ICacheBuilderExtended<T, V> : ICacheBuilder<T> {

    fun remove(entity: String, value: V)

    fun remove(entity: Player, value: V)

}