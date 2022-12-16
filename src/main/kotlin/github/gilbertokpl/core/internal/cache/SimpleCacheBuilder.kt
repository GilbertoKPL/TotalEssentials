package github.gilbertokpl.core.internal.cache

import github.gilbertokpl.core.external.cache.interfaces.CacheBuilder
import org.bukkit.Location
import org.bukkit.entity.Player

class SimpleCacheBuilder<T> : CacheBuilder<T> {
    private val hashMap = HashMap<String, T?>()

    override fun update() {
        TODO("Not yet implemented")
    }

    override fun load() {
        TODO("Not yet implemented")
    }

    override fun unload() {
        TODO("Not yet implemented")
    }

    override fun getMap(): HashMap<String, T?> {
        return hashMap
    }

    override fun get(entity: String): T? {
        return hashMap[entity.lowercase()]
    }

    override fun get(entity: Player): T? {
        return get(entity.name)
    }

    override fun delete(entity: String) {
        hashMap.remove(entity.lowercase())
    }

    override fun delete(entity: Player) {
        delete(entity.name)
    }

    override fun set(entity: Player, value: T) {
        set(entity.name, value)
    }

    override fun set(entity: String, value: T, override: Boolean) {
        set(entity, value)
    }

    override fun set(entity: String, value: T) {
        hashMap[entity.lowercase()] = value
    }
}