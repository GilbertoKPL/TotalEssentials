package me.gilberto.essentials.management

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object Dao {
    val inventory = HashMap<Player, String>(10)
    val editinventory = HashMap<Player, String>(10)

    val kitsitens = HashMap<String, ArrayList<ItemStack>>(30)
    val kitsrealname = HashMap<String, String>(30)
    val kitstime = HashMap<String, Long>(30)
}