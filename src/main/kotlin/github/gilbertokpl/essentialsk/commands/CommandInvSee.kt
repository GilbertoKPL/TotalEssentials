package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandInvSee : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.invseeActivated,
            consoleCanUse = false,
            commandName = "invsee",
            timeCoolDown = null,
            permission = "essentialsk.commands.invsee",
            minimumSize = 1,
            maximumSize = 1,
            commandUsage = listOf(
                "/invsee <playerName>",
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        //check if player is same
        if (args[0].equals(s.name.lowercase(), ignoreCase = true)) {
            s.sendMessage(LangConfig.invseeSameName)
            return false
        }

        val p = EssentialsK.instance.server.getPlayer(args[0])

        //check if player is online and not op
        if (p == null || p.isOp && !(s as Player).isOp || p.gameMode != GameMode.SURVIVAL) {
            s.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        val playerInstance = PlayerData[s as Player] ?: return false

        playerInstance.inInvsee = p
        s.openInventory(p.inventory)

        return false
    }
}