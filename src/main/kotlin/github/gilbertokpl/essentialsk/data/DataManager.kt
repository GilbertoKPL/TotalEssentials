package github.gilbertokpl.essentialsk.data

import github.gilbertokpl.essentialsk.data.objects.KitDataV2
import net.dv8tion.jda.api.entities.TextChannel
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object DataManager {

    //KitCache
    val kitCacheV2 = HashMap<String, KitDataV2>()

    //discord
    var discordChat: TextChannel? = null

    //spawn
    val spawnCache = HashMap<String, Location>(2)

    //warps
    val warpsCache = HashMap<String, Location>(40)

    //tpa
    val tpaHash = HashMap<Player, Player>()

    val tpAcceptHash = HashMap<Player, Int>()

    //in teleport
    val inTeleport = ArrayList<Player>()

    //editKitInv
    val editKitInventory = HashMap<Int, ItemStack>(50)

    //kitInv
    val kitGuiCache = HashMap<Int, Inventory>(10)

    //click kitInv
    val kitClickGuiCache = HashMap<Int, String>(40)

    //material helper
    val material = HashMap<String, Material>(10)

    //editKit
    val editKit = HashMap<Player, String>(10)

    val editKitChat = HashMap<Player, String>(10)

}
