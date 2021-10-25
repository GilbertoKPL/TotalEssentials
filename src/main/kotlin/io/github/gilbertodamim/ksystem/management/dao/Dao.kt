package io.github.gilbertodamim.ksystem.management.dao

import org.bukkit.entity.Player
import com.github.benmanes.caffeine.cache.Caffeine
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack


object Dao {
    //kits
    val kitsCache = Caffeine.newBuilder().maximumSize(500).buildAsync<String, KSystemKit>()
    val kitGuiCache = Caffeine.newBuilder().maximumSize(500).build<Int, Inventory>()
    val kitClickGuiCache = Caffeine.newBuilder().maximumSize(500).build<Int, String>()
    val EditKitGuiCache = Caffeine.newBuilder().maximumSize(40).build<Int, ItemStack>()
    val KitPlayerCache = Caffeine.newBuilder().build<String, HashMap<String, Long>>()
    val ChatEventKit = HashMap<Player, String>(10)
    val kitInventory = HashMap<Player, String>(10)
    val kitEditInventory = HashMap<Player, String>(10)
    var inUpdate = false
}