package io.github.gilbertodamim.essentials.management

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object Dao {
    val kitInventory = HashMap<Player, String>(5)
    val editKitInventory = HashMap<Player, String>(5)
    val kitsItems = HashMap<String, ArrayList<ItemStack>>(30)
    val kitsRealName = HashMap<String, String>(30)
    val kitsTime = HashMap<String, Long>(30)
}