package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandDelHome : ICommand {
    override val consoleCanUse: Boolean = false
    override val permission: String = "essentialsk.commands.delhome"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf("/delhome (homeName)")
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>) : Boolean {

        val nameHome = args[0].lowercase()

        val playerInstance = PlayerData(s as Player)

        val playerCache = playerInstance.getCache() ?: return false

        //check if home don't exist
        if (!playerCache.homeCache.contains(nameHome)) {
            s.sendMessage(GeneralLang.getInstance().homesNameDontExist)
            return false
        }

        playerInstance.delHome(nameHome)

        s.sendMessage(GeneralLang.getInstance().homesHomeRemoved.replace("%home%", nameHome))
        return false
    }
}