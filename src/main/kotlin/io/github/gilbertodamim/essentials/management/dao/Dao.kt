package io.github.gilbertodamim.essentials.management.dao

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object Dao {
    val kitInventory = HashMap<Player, String>(10)
    val kitsCache = HashMap<String, GDKit>(30)
    var inUpdate = false
}