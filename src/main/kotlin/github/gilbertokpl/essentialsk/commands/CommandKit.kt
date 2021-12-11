package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.data.KitData
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.manager.IInstance
import github.gilbertokpl.essentialsk.util.ItemUtil
import github.gilbertokpl.essentialsk.util.PluginUtil
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class CommandKit : ICommand {
    override val consoleCanUse: Boolean = false
    override val permission: String = "essentialsk.commands.kit"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf("/kit (kitName)")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //send gui
        if (args.isEmpty()) {
            Dao.getInstance().kitGuiCache[1].also {
                it ?: run {
                    s.sendMessage(GeneralLang.getInstance().kitsNotExistKits)
                    return false
                }
                (s as Player).openInventory(it)
            }
            return false
        }

        val dataInstance = KitData(args[0])

        //check if not exist
        if (!dataInstance.checkCache()) {
            s.sendMessage(GeneralLang.getInstance().kitsList.replace("%kits%", dataInstance.kitList().toString()))
            return false
        }

        //give kit
        pickupKit(s as Player, args[0].lowercase())
        return false
    }

    private fun kitGui(kit: String, guiNumber: String, p: Player) {
        //create gui
        val inv = EssentialsK.instance.server.createInventory(null, 45, "§eKit $kit $guiNumber")

        //load caches
        val kitCache = KitData(kit).getCache()
        val playerCache = PlayerData(p.name)

        //get all time of kits
        var timeAll = playerCache.getCache()?.let { it.kitsCache[kit] ?: 0L } ?: return

        timeAll += kitCache.time

        //all items
        for (items in kitCache.items) {
            inv.addItem(items)
        }


        for (to1 in 36..44) {
            if (to1 == 36) {
                inv.setItem(
                    to1,
                    ItemUtil.getInstance()
                        .item(Material.HOPPER, GeneralLang.getInstance().kitsInventoryIconBackName, true)
                )
                continue
            }
            if (to1 == 40) {
                if (p.hasPermission("essentialsk.commands.editkit")) {
                    inv.setItem(
                        to1,
                        ItemUtil.getInstance()
                            .item(Material.CHEST, GeneralLang.getInstance().kitsInventoryIconEditKitName, true)
                    )
                    continue
                }
            }
            if (to1 == 44) {
                if (p.hasPermission("essentialsk.kit.$kit")) {
                    if (timeAll <= System.currentTimeMillis() || timeAll == 0L) {
                        inv.setItem(
                            to1,
                            ItemUtil.getInstance().item(Material.ARROW, GeneralLang.getInstance().kitsCatchIcon, true)
                        )
                        continue
                    }
                    val array = ArrayList<String>()
                    val remainingTime = timeAll - System.currentTimeMillis()
                    for (i in GeneralLang.getInstance().kitsCatchIconLoreTime) {
                        array.add(
                            i.replace(
                                "%time%",
                                PluginUtil.getInstance()
                                    .convertMillisToString(remainingTime, MainConfig.getInstance().kitsUseShortTime)
                            )
                        )
                    }
                    inv.setItem(
                        to1,
                        ItemUtil.getInstance()
                            .item(Material.ARROW, GeneralLang.getInstance().kitsCatchIconNotCatch, array, true)
                    )
                    continue
                }
                inv.setItem(
                    to1,
                    ItemUtil.getInstance().item(
                        Material.ARROW,
                        GeneralLang.getInstance().kitsCatchIconNotCatch,
                        GeneralLang.getInstance().kitsCatchIconLoreNotPerm,
                        true
                    )
                )
                continue
            }
            inv.setItem(
                to1,
                ItemUtil.getInstance().item(Dao.getInstance().material["glass"]!!, "§eKIT", true)
            )
        }
        p.openInventory(inv)
    }

    private fun pickupKit(p: Player, kit: String) {

        // check if player don't have permission
        if (!p.hasPermission("essentialsk.kit.$kit")) {
            p.sendMessage(GeneralLang.getInstance().generalNotPerm)
            return
        }

        //load caches
        val kitCache = KitData(kit).getCache()
        val playerCache = PlayerData(p.name)

        //get all time of kit
        var timeAll = playerCache.getCache()?.let { it.kitsCache[kit] ?: 0L } ?: return
        timeAll += kitCache.time

        // if time is remaining
        if (timeAll >= System.currentTimeMillis() && !p.hasPermission("essentialsk.bypass.kitcatch")) {
            val remainingTime = timeAll - System.currentTimeMillis()
            p.sendMessage(
                GeneralLang.getInstance().kitsCatchMessage.replace(
                    "%time%",
                    PluginUtil.getInstance()
                        .convertMillisToString(remainingTime, MainConfig.getInstance().kitsUseShortTime)
                )
            )
            return
        }

        //send kit to player
        if (giveKit(
                p,
                kitCache.items,
                MainConfig.getInstance().kitsEquipArmorInCatch,
                MainConfig.getInstance().kitsDropItemsInCatch
            )
        ) {
            playerCache.setKitTime(kit, System.currentTimeMillis())
            p.sendMessage(GeneralLang.getInstance().kitsCatchSuccess.replace("%kit%", kitCache.fakeName))
        }
    }

    fun giveKit(p: Player, items: List<ItemStack>, armorAutoEquip: Boolean, drop: Boolean = false): Boolean {
        //bug armored check error
        val inv = p.inventory

        val itemsInternal = ArrayList<ItemStack>()

        val inventorySlotsUsed = inv.filterNotNull().size

        val armor = ArrayList<String>()

        val itemsArmorInternal = HashMap<String, ItemStack>()

        var inventorySpace = (Integer.valueOf(36).minus(inventorySlotsUsed))

        //check if player has space in Armor contents
        if (armorAutoEquip) {
            fun helper(to: ItemStack?, name: String) {
                if (to == null) {
                    armor.add(name)
                }
            }
            helper(inv.helmet, "HELMET")
            helper(inv.chestplate, "CHESTPLATE")
            helper(inv.leggings, "LEGGINGS")
            helper(inv.boots, "BOOTS")
        }

        //check if item is armor
        for (i in items) {
            if (armorAutoEquip) {
                var bolArmor = false
                val split = i.type.name.split("_")
                split.forEach {
                    if (it.contains("HELMET") || it.contains("CHESTPLATE") || it.contains("LEGGINGS") || it.contains("BOOTS")) {
                        if (armor.contains(it)) {
                            armor.remove(it)
                            itemsArmorInternal[it] = i
                            bolArmor = true
                        }
                    }
                }
                if (bolArmor) continue
            }
            itemsInternal.add(i)
        }

        // give armor parts
        fun giveArmour() {
            for (i in itemsArmorInternal) {
                if (i.key == "HELMET") {
                    inv.helmet = i.value
                    continue
                }
                if (i.key == "CHESTPLATE") {
                    inv.chestplate = i.value
                    continue
                }
                if (i.key == "LEGGINGS") {
                    inv.leggings = i.value
                    continue
                }
                if (i.key == "BOOTS") {
                    inv.boots = i.value
                    continue
                }
            }
        }

        //drop itens if full
        if (drop) {
            for (i in itemsInternal) {
                if (inventorySpace > 0) {
                    p.inventory.addItem(i)
                    inventorySpace -= 1
                    continue
                }
                p.world.dropItem(p.location, i)
            }
            giveArmour()
            return true
        }

        //check if inventory is full and add item
        if (inventorySpace >= itemsInternal.size) {
            for (i in itemsInternal) {
                p.inventory.addItem(i)
            }
            giveArmour()
            return true
        }

        //send message if inventory is full
        p.sendMessage(
            GeneralLang.getInstance().kitsCatchNoSpace.replace(
                "%slots%",
                (itemsInternal.size - inventorySpace).toString()
            )
        )
        return false
    }

    fun kitGuiEvent(e: InventoryClickEvent): Boolean {
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
                CommandEditKit.getInstance().editKitGui(p, inventoryName[1])
            }
            if (number == 44) {
                if (meta.displayName == GeneralLang.getInstance().kitsCatchIcon) {
                    pickupKit(p, inventoryName[1])
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

    companion object : IInstance<CommandKit> {
        private val instance = createInstance()
        override fun createInstance(): CommandKit = CommandKit()
        override fun getInstance(): CommandKit {
            return instance
        }
    }
}