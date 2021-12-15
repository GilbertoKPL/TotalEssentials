package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.data.KitData
import github.gilbertokpl.essentialsk.inventory.EditKitInventory.editKitGui
import github.gilbertokpl.essentialsk.inventory.EditKitInventory.editKitGuiItems
import github.gilbertokpl.essentialsk.inventory.KitGuiInventory.kitGui
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.ItemUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class ClickInventoryEvent : Listener {
    @EventHandler
    fun event(e: InventoryClickEvent) {
        if (MainConfig.getInstance().kitsActivated) {
            try {
                if (editKitInventoryClickEvent(e)) return
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
            try {
                if (kitGuiEvent(e)) return
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        if (MainConfig.getInstance().containersBlockShiftEnable) {
            try {
                blockShiftInInventory(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
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
                p.openInventory(Dao.getInstance().kitGuiCache[inventoryName[2].toInt()]!!)
            }
            if (number == 40 && meta.displayName == GeneralLang.getInstance().kitsInventoryIconEditKitName && p.hasPermission(
                    "essentialsk.commands.editkit"
                )
            ) {
                editKitGui(p, inventoryName[1])
            }
            if (number == 44) {
                if (meta.displayName == GeneralLang.getInstance().kitsCatchIcon) {
                    ItemUtil.getInstance().pickupKit(p, inventoryName[1])
                    p.closeInventory()
                    return true
                }
                if (meta.displayName == GeneralLang.getInstance().kitsCatchIconNotCatch) {
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
                    Dao.getInstance().kitClickGuiCache[(number + 1) + ((inventoryName[1].toInt() - 1) * 27)]
                if (kit != null) {
                    kitGui(kit, inventoryName[1], p)
                }
                return true
            }
            if (number == 27 && inventoryName[1].toInt() > 1) {
                p.openInventory(Dao.getInstance().kitGuiCache[inventoryName[1].toInt() - 1]!!)
            }
            if (number == 35) {
                val check = Dao.getInstance().kitGuiCache[inventoryName[1].toInt() + 1]
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
        val inventoryName = e.view.title.split(" ")
        if (inventoryName[0].equals("§eEditKit", true)) {
            e.isCancelled = true
            val dataInstance = KitData(inventoryName[1])
            if (!dataInstance.checkCache()) {
                return false
            }
            val number = e.slot
            val p = e.whoClicked as Player

            //items
            if (number == 11) {
                p.closeInventory()
                editKitGuiItems(p, inventoryName[1], dataInstance.getCache().items)
                Dao.getInstance().editKit[p] = inventoryName[1]
            }

            //time
            if (number == 13) {
                p.closeInventory()
                p.sendMessage(GeneralLang.getInstance().kitsEditKitInventoryTimeMessage)
                Dao.getInstance().editKitChat[p] = "time-${inventoryName[1]}"
            }

            //name
            if (number == 15) {
                p.closeInventory()
                p.sendMessage(GeneralLang.getInstance().kitsEditKitInventoryNameMessage)
                Dao.getInstance().editKitChat[p] = "name-${inventoryName[1]}"
            }

        }

        return false
    }

    //block shift
    private fun blockShiftInInventory(e: InventoryClickEvent) {
        if ((e.currentItem ?: return).type == Material.AIR) return
        if (e.click.isShiftClick &&
            MainConfig.getInstance().containersBlockShift.contains(e.inventory.type.name.lowercase()) &&
            !e.whoClicked.hasPermission("essentialsk.bypass.shiftcontainer")
        ) {
            e.whoClicked.sendMessage(GeneralLang.getInstance().generalNotPermAction)
            e.isCancelled = true
            return
        }
    }
}