package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.manager.ICommand
import github.genesyspl.cardinal.util.ItemUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandGiveKit : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "givekit"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.givekit"
    override val minimumSize = 2
    override val maximumSize = 2
    override val commandUsage = listOf("/givekit <playerName> <kitName>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //check length of kit name
        if (args[0].length > 16) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsNameLength)
            return false
        }

        val dataInstance = DataManager.getInstance().kitCacheV2[args[1]]

        //check if not exist
        if (dataInstance == null) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsNotExist)
            return false
        }

        //check if player not exist
        val p = github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalPlayerNotOnline)
            return false
        }

        //give kit
        ItemUtil.getInstance().giveKit(p, dataInstance.items, true, drop = true)
        s.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsGiveKitMessageOther.replace(
                "%kit%",
                dataInstance.fakeName
            ).replace("%player%", p.name)
        )
        p.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsGiveKitMessage.replace(
                "%kit%",
                dataInstance.fakeName
            )
        )
        return false
    }
}