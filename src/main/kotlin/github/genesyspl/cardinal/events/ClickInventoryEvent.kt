package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.inventory.EditKitInventory.editKitGui
import github.genesyspl.cardinal.inventory.EditKitInventory.editKitGuiItems
import github.genesyspl.cardinal.inventory.KitGuiInventory.kitGui
import github.genesyspl.cardinal.util.FileLoggerUtil
import github.genesyspl.cardinal.util.ItemUtil
import github.genesyspl.cardinal.util.PermissionUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType


class ClickInventoryEvent : Listener {
    @EventHandler
    fun event(e: InventoryClickEvent) {
        if (e.slot == 45) {
            return
        }
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().kitsActivated) {
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
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().containersBlockShiftEnable) {
            try {
                blockShiftInInventory(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().addonsColorInAnvil) {
            try {
                anvilColor(e)
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
                p.openInventory(DataManager.getInstance().kitGuiCache[inventoryName[2].toInt()]!!)
            }
            if (number == 40 && meta.displayName == github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsInventoryIconEditKitName && p.hasPermission(
                    "cardinal.commands.editkit"
                )
            ) {
                editKitGui(p, inventoryName[1])
            }
            if (number == 44) {
                if (meta.displayName == github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsCatchIcon) {
                    ItemUtil.getInstance().pickupKit(p, inventoryName[1])
                    p.closeInventory()
                    return true
                }
                if (meta.displayName == github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsCatchIconNotCatch) {
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
                    DataManager.getInstance().kitClickGuiCache[(number + 1) + ((inventoryName[1].toInt() - 1) * 27)]
                if (kit != null) {
                    kitGui(kit, inventoryName[1], p)
                }
                return true
            }
            if (number == 27 && inventoryName[1].toInt() > 1) {
                p.openInventory(DataManager.getInstance().kitGuiCache[inventoryName[1].toInt() - 1]!!)
            }
            if (number == 35) {
                val check = DataManager.getInstance().kitGuiCache[inventoryName[1].toInt() + 1]
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
            val dataInstance = DataManager.getInstance().kitCacheV2[inventoryName[1]] ?: return false
            val number = e.slot
            val p = e.whoClicked as Player

            //items
            if (number == 10) {
                p.closeInventory()
                editKitGuiItems(p, inventoryName[1], dataInstance.items)
                DataManager.getInstance().editKit[p] = inventoryName[1]
            }

            //time
            if (number == 12) {
                p.closeInventory()
                p.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsEditKitInventoryTimeMessage)
                DataManager.getInstance().editKitChat[p] = "time-${inventoryName[1]}"
            }

            //name
            if (number == 14) {
                p.closeInventory()
                p.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsEditKitInventoryNameMessage)
                DataManager.getInstance().editKitChat[p] = "name-${inventoryName[1]}"
            }

            //weight
            if (number == 16) {
                p.closeInventory()
                p.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsEditKitInventoryWeightMessage)
                DataManager.getInstance().editKitChat[p] = "weight-${inventoryName[1]}"
            }

        }

        return false
    }

    //block shift
    private fun blockShiftInInventory(e: InventoryClickEvent) {
        if ((e.currentItem ?: return).type == Material.AIR) return
        if (e.click.isShiftClick &&
            github.genesyspl.cardinal.configs.MainConfig.getInstance().containersBlockShift.contains(e.inventory.type.name.lowercase()) &&
            !e.whoClicked.hasPermission("cardinal.bypass.shiftcontainer")
        ) {
            e.whoClicked.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalNotPermAction)
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
            meta.setDisplayName(PermissionUtil.getInstance().colorPermission(e.whoClicked as Player, name))
            item.itemMeta = meta
            e.currentItem = item
        }
    }
}