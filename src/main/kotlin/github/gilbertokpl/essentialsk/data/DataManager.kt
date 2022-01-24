package github.gilbertokpl.essentialsk.data

import net.dv8tion.jda.api.entities.TextChannel
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object DataManager {

    //editKitInv
    internal val editKitInventory = HashMap<Int, ItemStack>(50)

    //kitInv
    internal val kitGuiCache = HashMap<Int, Inventory>(10)

    //click kitInv
    internal val kitClickGuiCache = HashMap<Int, String>(40)

    //editKit
    internal val editKit = HashMap<Player, String>(10)

    internal val editKitChat = HashMap<Player, String>(10)

    internal val hashTextChannel = HashMap<String, TextChannel>()

}
