package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.manager.ICommand
import github.genesyspl.cardinal.util.ItemUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandKit : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "kit"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.kit"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf("/kit", "/kit <kitName>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (s !is Player) {
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsList.replace(
                    "%kits%",
                    DataManager.getInstance().kitCacheV2.map { it.key }.toString()
                )
            )
            return false
        }

        //send gui
        if (args.isEmpty()) {
            DataManager.getInstance().kitGuiCache[1].also {
                it ?: run {
                    s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsNotExistKits)
                    return false
                }
                s.openInventory(it)
            }
            return false
        }

        val dataInstance = DataManager.getInstance().kitCacheV2[args[0]]

        //check if not exist
        if (dataInstance == null) {
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsList.replace(
                    "%kits%",
                    DataManager.getInstance().kitCacheV2.map { it.key }.toString()
                )
            )
            return false
        }

        //give kit
        ItemUtil.getInstance().pickupKit(s, args[0].lowercase())
        return false
    }
}