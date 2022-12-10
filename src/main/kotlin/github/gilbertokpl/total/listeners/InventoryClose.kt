package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.local.KitsData
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.internal.DataManager
import github.gilbertokpl.total.cache.internal.KitGuiInventory

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

class InventoryClose : Listener {
    @EventHandler
    fun event(e: InventoryCloseEvent) {
        if (MainConfig.kitsActivated) {
            try {
                if (editKitInventoryCloseEvent(e)) return
            } catch (e: Throwable) {

            }
        }
        if (MainConfig.invseeActivated) {
            try {
                invseeInventoryCloseEvent(e)
            } catch (e: Throwable) {

            }
        }
    }

    private fun editKitInventoryCloseEvent(e: InventoryCloseEvent): Boolean {
        val p = e.player as Player
        DataManager.editKit[p].also {
            if (it == null) return false
            DataManager.editKit.remove(p)

            val array = ArrayList<ItemStack>()

            for (i in e.inventory.contents.filterNotNull()) {
                array.add(i)
            }

            KitsData.kitItems[it] = array

            val name =  KitsData.kitFakeName[it]

            e.player.sendMessage(
                LangConfig.kitsEditKitSuccess.replace(
                    "%kit%",
                    if (name == null || name == "") it else name
                )
            )
        }

        KitGuiInventory.setup()

        return true
    }

    private fun invseeInventoryCloseEvent(e: InventoryCloseEvent) {
        val p = e.player as Player

        if (PlayerData.inInvSee[p] != null && e.inventory.type == InventoryType.PLAYER) {
            PlayerData.inInvSee[p] = null
        }
    }
}
