package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandBack : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "back"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.back"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf("/back")
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val playerCache = DataManager.getInstance().playerCacheV2[s.name.lowercase()] ?: return false

        val loc = playerCache.backLocation ?: run {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().backSendNotToBack)
            return false
        }

        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().backDisabledWorlds.contains(loc.world!!.name.lowercase())) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().backSendNotToBack)
            playerCache.clearBack()
            return false
        }

        (s as Player).teleport(loc)
        playerCache.clearBack()
        s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().backSendSuccess)
        return false
    }
}