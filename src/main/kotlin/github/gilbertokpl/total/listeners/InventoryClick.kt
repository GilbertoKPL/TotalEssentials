package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.local.KitsData
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.internal.DataManager
import github.gilbertokpl.total.cache.internal.EditKitInventory.editKitGui
import github.gilbertokpl.total.cache.internal.EditKitInventory.editKitGuiItems
import github.gilbertokpl.total.cache.internal.KitGuiInventory.kitGui
import github.gilbertokpl.total.cache.local.LoginData

import github.gilbertokpl.total.util.ItemUtil
import github.gilbertokpl.total.util.PermissionUtil

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType

class InventoryClick : Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun event(e: InventoryClickEvent) {

        if (e.whoClicked is Player && !LoginData.checkIfPlayerIsLoggedIn(e.whoClicked as Player)) {
            e.isCancelled = true
            return
        }

        if (e.slot == 45) {
            return
        }
        if (MainConfig.kitsActivated) {
            try {
                if (editKitInventoryClickEvent(e)) return
            } catch (_: Throwable) {

            }
            try {
                if (kitGuiEvent(e)) return
            } catch (_: Throwable) {

            }
        }
        if (MainConfig.containersBlockShiftEnable) {
            try {
                blockShiftInInventory(e)
            } catch (_: Throwable) {

            }
        }
        if (MainConfig.addonsColorInAnvil) {
            try {
                anvilColor(e)
            } catch (_: Throwable) {

            }
        }
        if (MainConfig.invseeActivated) {
            try {
                invSeeEvent(e)
            } catch (_: Throwable) {

            }
        }
    }

    //kit event
    private fun kitGuiEvent(e: InventoryClickEvent): Boolean {
        e.currentItem ?: return false
        val inventoryName = e.view.title.split(" ")
        if (inventoryName[0] == "§eKit") {
            val meta = e.currentItem!!.itemMeta ?: return false
            val p = e.whoClicked as Player
            e.isCancelled = true

            val number = e.slot
            if (number == 36) {
                p.openInventory(DataManager.kitGuiCache[inventoryName[2].toInt()]!!)
            }
            if (number == 40 && meta.displayName == LangConfig.kitsInventoryIconEditKitName && p.hasPermission(
                    "totalessentials.commands.editkit"
                )
            ) {
                editKitGui(p, inventoryName[1])
            }
            if (number == 44) {
                if (meta.displayName == LangConfig.kitsGetIcon) {
                    ItemUtil.pickupKit(p, inventoryName[1])
                    p.closeInventory()
                    return true
                }
                if (meta.displayName == LangConfig.kitsGetIconNotCatch) {
                    kitGui(inventoryName[1], inventoryName[2], p)
                }
            }
            return true
        }
        if (inventoryName[0] == "§eKits") {
            val p = e.whoClicked as Player
            e.isCancelled = true
            val number = e.slot
            if (number < 27) {
                val kit =
                    DataManager.kitClickGuiCache[(number + 1) + ((inventoryName[1].toInt() - 1) * 27)]
                if (kit != null) {
                    kitGui(kit, inventoryName[1], p)
                }
                return true
            }
            if (number == 27 && inventoryName[1].toInt() > 1) {
                p.openInventory(DataManager.kitGuiCache[inventoryName[1].toInt() - 1]!!)
            }
            if (number == 35) {
                val check = DataManager.kitGuiCache[inventoryName[1].toInt() + 1]
                if (check != null) {
                    p.openInventory(check)
                }
            }
            return true
        }
        return false
    }

    //editkit event
    private fun editKitInventoryClickEvent(e: InventoryClickEvent): Boolean {
        e.currentItem ?: return false
        e.view.title
        val inventoryName = e.view.title.split(" ")
        if (inventoryName[0].equals("§eEditKit", true)) {
            e.isCancelled = true
            val number = e.slot
            val p = e.whoClicked as Player

            //items
            if (number == 10) {
                p.closeInventory()
                editKitGuiItems(p, inventoryName[1], KitsData.kitItems[inventoryName[1]]!!)
                DataManager.editKit[p] = inventoryName[1]
            }

            //time
            if (number == 12) {
                p.closeInventory()
                p.sendMessage(LangConfig.kitsEditKitInventoryTimeMessage)
                DataManager.editKitChat[p] = "time-${inventoryName[1]}"
            }

            //name
            if (number == 14) {
                p.closeInventory()
                p.sendMessage(LangConfig.kitsEditKitInventoryNameMessage)
                DataManager.editKitChat[p] = "name-${inventoryName[1]}"
            }

            //weight
            if (number == 16) {
                p.closeInventory()
                p.sendMessage(LangConfig.kitsEditKitInventoryWeightMessage)
                DataManager.editKitChat[p] = "weight-${inventoryName[1]}"
            }

        }

        return false
    }

    //block shift
    private fun blockShiftInInventory(e: InventoryClickEvent) {
        if ((e.currentItem ?: return).type == Material.AIR) return
        if (e.click.isShiftClick &&
            MainConfig.containersBlockShift.contains(e.inventory.type.name.lowercase()) &&
            !e.whoClicked.hasPermission("totalessentials.bypass.shiftcontainer")
        ) {
            e.whoClicked.sendMessage(LangConfig.generalNotPermAction)
            e.isCancelled = true
            return
        }
    }

    //anvil color

    private fun anvilColor(e: InventoryClickEvent) {
        if (e.inventory.type == InventoryType.ANVIL && e.slotType == InventoryType.SlotType.RESULT) {
            val item = e.currentItem ?: return
            if (item.type == Material.AIR || !item.itemMeta!!.hasDisplayName()) {
                return
            }
            val meta = item.itemMeta ?: return
            val name = meta.displayName
            val oldItem = e.inventory.getItem(0) ?: return
            val oldMeta = oldItem.itemMeta ?: return
            if (oldMeta.hasDisplayName()) {
                val oldName = oldMeta.displayName.replace("§", "")
                if (name == oldName) {
                    meta.setDisplayName(oldMeta.displayName)
                    item.itemMeta = meta
                    e.currentItem = item
                    return
                }
            }
            meta.setDisplayName(PermissionUtil.colorPermission(e.whoClicked as Player, name))
            item.itemMeta = meta
            e.currentItem = item
        }
    }

    private fun invSeeEvent(e: InventoryClickEvent) {
        val p = e.whoClicked as Player
        val otherPlayer = PlayerData.inInvsee[p]
        if (e.inventory.type == InventoryType.PLAYER && otherPlayer != null) {

            if (!otherPlayer.isOnline) {
                p.closeInventory()
                p.sendMessage(LangConfig.invseePlayerLeave)
            }

            if (p.hasPermission("totalessentials.commands.invsee")
                && !p.hasPermission("totalessentials.commands.invsee.admin")
            ) {
                e.isCancelled = true
            }
        }
    }
}
