package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PlayerUtil.teleportSafe
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandBack : CommandManager("back") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("voltar"),
            active = MainConfig.backActivated,
            target = CommandTargetType.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.back",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf("/back")
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        val player = sender as Player

        // Get last back location
        val lastLocation = PlayerData.backLocation[player] ?: run {
            player.sendMessage(LangConfig.backNotToBack)
            return false
        }

        // Check if world is disabled for back command
        val worldName = lastLocation.world?.name?.lowercase()
        if (worldName == null || MainConfig.backDisabledWorlds.contains(worldName)) {
            player.sendMessage(LangConfig.backNotToBack)
            PlayerData.backLocation[player] = null
            return false
        }

        // Teleport safely
        player.teleportSafe(lastLocation)

        // Clear back location
        PlayerData.backLocation[player] = null

        player.sendMessage(LangConfig.backSuccess)
        return false
    }
}
