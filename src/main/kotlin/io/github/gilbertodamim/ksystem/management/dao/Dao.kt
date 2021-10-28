package io.github.gilbertodamim.ksystem.management.dao

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack


object Dao {
    //kits
    val kitsCache = Caffeine.newBuilder().initialCapacity(20).maximumSize(500).buildAsync<String, KSystemKit>()
    val kitGuiCache = Caffeine.newBuilder().initialCapacity(20).maximumSize(500).build<Int, Inventory>()
    val kitClickGuiCache = Caffeine.newBuilder().initialCapacity(20).maximumSize(500).build<Int, String>()
    val kitPlayerCache = Caffeine.newBuilder().build<String, Cache<String, Long>>()

    //kits not cache
    val EditKitGuiCache = Caffeine.newBuilder().maximumSize(40).build<Int, ItemStack>()
    val ChatEventKit = HashMap<Player, String>(10)
    val kitInventory = HashMap<Player, String>(10)
    val kitEditInventory = HashMap<Player, String>(10)

    //general
    val Materials = HashMap<String, Material>(10)
}