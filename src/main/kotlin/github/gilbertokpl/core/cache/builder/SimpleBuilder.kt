package github.gilbertokpl.core.cache.builder

import github.gilbertokpl.core.cache.interfaces.ICacheBuilder
import org.bukkit.entity.Player

class SimpleBuilder<T> : ICacheBuilder<T> {
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

    override fun getMap(): Map<String, T?> {
        return hashMap.toMap()
    }

    override fun get(entity: String): T? {
        return hashMap[entity.lowercase()]
    }

    override fun get(entity: Player): T? {
        return get(entity.name)
    }

    override fun remove(entity: String) {
        hashMap.remove(entity.lowercase())
    }

    override fun remove(entity: Player) {
        remove(entity.name)
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