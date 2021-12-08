package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHome : ICommand {
    override val consoleCanUse: Boolean = false
    override val permission: String = "essentialsk.commands.home"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf("/home (homeName)")
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>) : Boolean {

        if (Dao.getInstance().inTeleport.contains(s)) {
            s.sendMessage(GeneralLang.getInstance().homesInTeleport)
            return false
        }

        val nameHome = args[0].lowercase()

        val playerInstance = PlayerData(s as Player)

        val playerCache = playerInstance.getCache() ?: return false

        //check if home don't exist
        if (!playerCache.homeCache.contains(nameHome)) {
            s.sendMessage(GeneralLang.getInstance().homesNameDontExist)
            return false
        }

        if (s.hasPermission("essentialsk.bypass.teleport")) {
            s.teleport(playerCache.homeCache[nameHome]!!)
            s.sendMessage(GeneralLang.getInstance().homesTeleported.replace("%home%", nameHome))
            return false
        }

        val time = MainConfig.getInstance().homesTimeToTeleport

        Dao.getInstance().inTeleport.add(s)

        val exe = TaskUtil.getInstance().teleportExecutor(time)

        exe {
            Dao.getInstance().inTeleport.remove(s)
            s.teleport(playerCache.homeCache[nameHome]!!)
            s.sendMessage(GeneralLang.getInstance().homesTeleported.replace("%home%", nameHome))
        }

        s.sendMessage(GeneralLang.getInstance().homesTimeToTeleport.replace("%home%", nameHome).replace("%time%", time.toString()))

        return false
    }
}