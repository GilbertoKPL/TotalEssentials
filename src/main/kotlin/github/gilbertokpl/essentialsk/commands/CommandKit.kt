package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.dao.KitData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.util.ItemUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandKit : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.kitsActivated,
            consoleCanUse = true,
            commandName = "kit",
            timeCoolDown = null,
            permission = "essentialsk.commands.kit",
            minimumSize = 0,
            maximumSize = 1,
            commandUsage = listOf(
                "/kit",
                "/kit <kitName>"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (s !is Player || (args.isEmpty() && !MainConfig.kitsMenuKit)) {
            s.sendMessage(
                GeneralLang.kitsList.replace(
                    "%kits%",
                    KitData.getList().toString()
                )
            )
            return false
        }

        //send gui
        if (args.isEmpty()) {

            DataManager.kitGuiCache[1].also {
                it ?: run {
                    s.sendMessage(GeneralLang.kitsNotExistKits)
                    return false
                }
                s.openInventory(it)
            }
            return false
        }

        val dataInstance = KitData[args[0]]

        //check if not exist
        if (dataInstance == null) {
            s.sendMessage(
                GeneralLang.kitsList.replace(
                    "%kits%",
                    KitData.getList().toString()
                )
            )
            return false
        }

        //give kit
        ItemUtil.pickupKit(s, args[0].lowercase())
        return false
    }
}
