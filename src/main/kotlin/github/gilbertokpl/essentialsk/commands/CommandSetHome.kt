package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.PluginUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetHome : ICommand {
    override val consoleCanUse: Boolean = false
    override val permission: String = "essentialsk.commands.sethome"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf("/sethome (homeName)")
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>) : Boolean {

        val nameHome = args[0].lowercase()

        //check if home name do not contain . or - to not bug
        if (PluginUtil.getInstance().checkSpecialCaracteres(nameHome)) {
            s.sendMessage(GeneralLang.getInstance().generalSpecialCaracteresDisabled)
            return false
        }

        //check length of home name
        if (nameHome.length > 16) {
            s.sendMessage(GeneralLang.getInstance().homesNameLength)
            return false
        }

        val playerInstance = PlayerData(s as Player)

        val playerCache = playerInstance.getCache() ?: return false

        //check if already exist
        if (playerCache.homeCache.contains(nameHome)) {
            s.sendMessage(GeneralLang.getInstance().homesNameAlreadyExist)
            return false
        }

        //check limit of homes
        if (playerCache.homeCache.size > playerCache.homeLimit) {
            s.sendMessage(GeneralLang.getInstance().homesHomeLimitCreated.replace("%limit%", playerCache.homeLimit.toString()))
            return false
        }

        //check if world is blocked
        if (MainConfig.getInstance().homesBlockWorlds.contains(s.world.name.lowercase())) {
            s.sendMessage(GeneralLang.getInstance().homesHomeWorldBlocked)
            return false
        }

        playerInstance.createHome(nameHome, s.location)
        s.sendMessage(GeneralLang.getInstance().homesHomeCreated.replace("%home%", nameHome))

        return false
    }
}