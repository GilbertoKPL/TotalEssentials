package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.player.modify.BackCache.clearBack
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandBack : CommandCreator {

    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.backActivated,
            consoleCanUse = false,
            commandName = "back",
            timeCoolDown = null,
            permission = "essentialsk.commands.back",
            minimumSize = 0,
            maximumSize = 0,
            commandUsage = listOf("/back")
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val p = s as Player

        val playerCache = PlayerData[p] ?: return false

        val loc = playerCache.backLocation ?: run {
            p.sendMessage(GeneralLang.backSendNotToBack)
            return false
        }

        if (MainConfig.backDisabledWorlds.contains(loc.world!!.name.lowercase())) {
            p.sendMessage(GeneralLang.backSendNotToBack)
            playerCache.clearBack()
            return false
        }

        p.teleport(loc)

        playerCache.clearBack()

        p.sendMessage(GeneralLang.backSendSuccess)
        return false
    }
}
