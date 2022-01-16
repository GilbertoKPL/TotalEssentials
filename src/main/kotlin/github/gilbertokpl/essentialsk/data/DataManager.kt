package github.gilbertokpl.essentialsk.data

import net.dv8tion.jda.api.entities.TextChannel
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object DataManager {

    //in teleport
    val inTeleport = ArrayList<Player>()

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
