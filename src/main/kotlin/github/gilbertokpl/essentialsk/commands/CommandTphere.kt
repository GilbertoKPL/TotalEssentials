package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTphere : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.tphereActivated,
            consoleCanUse = false,
            commandName = "tphere",
            timeCoolDown = null,
            permission = "essentialsk.commands.tphere",
            minimumSize = 1,
            maximumSize = 1,
            commandUsage = listOf("/tphere <playerName>")
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // check if player is online
        val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(GeneralLang.generalPlayerNotOnline)
            return false
        }

        p.teleport((s as Player).location)

        p.sendMessage(
            GeneralLang.tphereTeleportedOtherSuccess
        )
        s.sendMessage(
            GeneralLang.tphereTeleportedSuccess.replace("%player%", p.name)
        )
        return false
    }
}
