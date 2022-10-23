package github.gilbertokpl.base.external.cache.interfaces

import org.bukkit.entity.Player

interface CacheBuilderV2<T, V> : CacheBuilder<T> {
    fun remove(entity: String, value: V)

    fun remove(entity: Player, value: V)
}