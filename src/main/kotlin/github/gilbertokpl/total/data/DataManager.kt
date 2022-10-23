package github.gilbertokpl.total.data

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.Database

internal object DataManager {

    lateinit var sql: Database

    private var update = false

    //editKitInv
    val editKitInventory = HashMap<Int, ItemStack>(50)

    //kitInv
    val kitGuiCache = HashMap<Int, Inventory>(10)

    //click kitInv
    val kitClickGuiCache = HashMap<Int, String>(40)

    //editKit
    val editKit = HashMap<Player, String>(10)

    val editKitChat = HashMap<Player, String>(10)

}
