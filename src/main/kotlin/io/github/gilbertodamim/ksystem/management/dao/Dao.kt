package io.github.gilbertodamim.ksystem.management.dao

import org.bukkit.entity.Player

object Dao {
    val kitInventory = HashMap<Player, String>(10)
    val kitEditInventory = HashMap<Player, String>(10)
    val kitsCache = HashMap<String, KSystemKit>(30)
    var inUpdate = false
}