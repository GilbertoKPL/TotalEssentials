package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.data.KitData
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.manager.IInstance
import github.gilbertokpl.essentialsk.util.PluginUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.inventory.ItemStack

class CommandEditKit : ICommand {

    private val editKit = HashMap<Player, String>(10)
    private val editKitChat = HashMap<Player, String>(10)

    override val consoleCanUse: Boolean = false
    override val permission: String = "essentialsk.commands.editkit"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf("/editkit <kitName>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //check length of kit name
        if (args[0].length > 16) {
            s.sendMessage(GeneralLang.getInstance().kitsNameLength)
            return false
        }

        val dataInstance = KitData(args[0])

        //check if not exist
        if (dataInstance.checkCache()) {
            s.sendMessage(GeneralLang.getInstance().kitsNotExist)
            return false
        }

        //open inventory
        editKitGui(s as Player, args[0].lowercase())
        return false
    }

    private fun editKitGuiItems(p: Player, kit: String, items: List<ItemStack>) {
        val inv = EssentialsK.instance.server.createInventory(null, 36, kit)
        items.forEach {
            inv.addItem(it)
        }
        p.openInventory(inv)
    }

    fun editKitGui(p: Player, kit: String) {
        val inv = EssentialsK.instance.server.createInventory(null, 27, "§eEditKit $kit")
        for (i in Dao.getInstance().editKitInventory) {
            inv.setItem(i.key, i.value)
        }
        p.openInventory(inv)
    }

    fun editKitInventoryClickEvent(e: InventoryClickEvent): Boolean {
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
                editKit[p] = inventoryName[1]
            }

            //time
            if (number == 13) {
                p.closeInventory()
                p.sendMessage(GeneralLang.getInstance().kitsEditKitInventoryTimeMessage)
                editKitChat[p] = "time-${inventoryName[1]}"
            }

            //name
            if (number == 15) {
                p.closeInventory()
                p.sendMessage(GeneralLang.getInstance().kitsEditKitInventoryNameMessage)
                editKitChat[p] = "name-${inventoryName[1]}"
            }

        }

        return false
    }

    fun editKitChatEvent(e: AsyncPlayerChatEvent): Boolean {
        editKitChat[e.player].also {
            if (it == null) return false
            editKitChat.remove(e.player)
            val split = it.split("-")

            val dataInstance = KitData(split[1])

            //time
            if (split[0] == "time") {
                e.isCancelled = true
                val time = PluginUtil.getInstance().convertStringToMillis(e.message)
                e.player.sendMessage(GeneralLang.getInstance().generalSendingInfoToDb)
                if (dataInstance.setTime(time)) {
                    e.player.sendMessage(
                        GeneralLang.getInstance().kitsEditKitTime.replace(
                            "%time%",
                            PluginUtil.getInstance()
                                .convertMillisToString(time, MainConfig.getInstance().kitsUseShortTime)
                        )
                    )
                    e.player.sendMessage(GeneralLang.getInstance().kitsEditKitSuccess.replace("%kit%", split[1]))
                }
                return true
            }

            //name
            if (split[0] == "name") {
                e.isCancelled = true
                //check message length
                if (e.message.length > 16) {
                    e.player.sendMessage(GeneralLang.getInstance().kitsNameLength)
                    return true
                }

                e.player.sendMessage(GeneralLang.getInstance().generalSendingInfoToDb)
                if (dataInstance.setFakeName(e.message.replace("&", "§"))) {
                    e.player.sendMessage(GeneralLang.getInstance().kitsEditKitSuccess.replace("%kit%", split[1]))
                }
            }

            return true
        }
    }

    fun editKitInventoryCloseEvent(e: InventoryCloseEvent): Boolean {
        editKit[e.player].also {
            if (it == null) return false
            editKit.remove(e.player)
            e.player.sendMessage(GeneralLang.getInstance().generalSendingInfoToDb)
            if (KitData(it).setItems(e.inventory.contents.filterNotNull().toList())) {
                e.player.sendMessage(GeneralLang.getInstance().kitsEditKitSuccess.replace("%kit%", it))
            }
            return true
        }
    }

    companion object : IInstance<CommandEditKit> {
        private val instance = createInstance()
        override fun createInstance(): CommandEditKit = CommandEditKit()
        override fun getInstance(): CommandEditKit {
            return instance
        }
    }
}