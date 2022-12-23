package github.gilbertokpl.total.cache.internal

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.Database

internal object DataManager {

    lateinit var sql: Database

    //vip
    val tokenVip = HashMap<String, Long>(10)
    val vipEdit = HashMap<Player, String>(10)

    //ShopInv
    val shopGuiCache = HashMap<Int, Inventory>(10)
    val ClickShopGuiCache = LinkedHashMap<Int, String>(40)

    //kitInv
    val kitGuiCache = HashMap<Int, Inventory>(10)
    val ClickKitGuiCache = HashMap<Int, String>(40)

    //PlaytimeInv
    val playTimeGuiCache = LinkedHashMap<Int, Inventory>(10)

    //editKit
    val editKit = HashMap<Player, String>(10)
    val editKitInventory = HashMap<Int, ItemStack>(50)
    val editKitChat = HashMap<Player, String>(10)

}
