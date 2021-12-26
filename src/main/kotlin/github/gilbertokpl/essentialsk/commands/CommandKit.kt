package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.data.KitData
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.ItemUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandKit : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "kit"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.kit"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf("/kit", "/kit <kitName>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (s !is Player) {
            s.sendMessage(GeneralLang.getInstance().kitsList.replace("%kits%", KitData("").kitList().toString()))
            return false
        }

        //send gui
        if (args.isEmpty()) {
            Dao.getInstance().kitGuiCache[1].also {
                it ?: run {
                    s.sendMessage(GeneralLang.getInstance().kitsNotExistKits)
                    return false
                }
                s.openInventory(it)
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
        ItemUtil.getInstance().pickupKit(s, args[0].lowercase())
        return false
    }
}