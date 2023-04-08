package github.gilbertokpl.total.cache.internal

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.Database

internal object DataManager {

    lateinit var sql: Database

    //vip
    val tokenVip: MutableMap<String, Long> = mutableMapOf()
    val playerVipEdit: MutableMap<Player, String> = mutableMapOf()

    //ShopInv
    val shopInventoryCache: MutableMap<Int, Inventory> = linkedMapOf()
    val shopItemCache: MutableMap<Int, String> = hashMapOf()

    //kitInv
    val kitInventoryCache: MutableMap<Int, Inventory> = linkedMapOf()
    val kitItemCache: MutableMap<Int, String> = hashMapOf()

    //PlaytimeInv
    val playTimeInventoryCache: MutableMap<Int, Inventory> = linkedMapOf()

    //editKit
    val playerEditKit: MutableMap<Player, String> = mutableMapOf()
    val editKitItemCache: MutableMap<Int, ItemStack> = hashMapOf()
    val playerEditKitChat: MutableMap<Player, String> = mutableMapOf()

}





