package io.github.gilbertodamim.kcore.dao

import org.bukkit.inventory.ItemStack

data class KCoreKit(val name: String, val time: Long, val realName: String, val items: Array<ItemStack?>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as KCoreKit
        if (name != other.name) return false
        if (time != other.time) return false
        if (realName != other.realName) return false
        if (!items.contentEquals(other.items)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + realName.hashCode()
        result = 31 * result + items.contentHashCode()
        return result
    }
}