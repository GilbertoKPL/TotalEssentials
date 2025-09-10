package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.cache.data.SpawnData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetSpawn : CommandManager("setspawn") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(""),
            active = MainConfig.spawnActivated,
            target = CommandTargetType.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.setspawn",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf("/setspawn")
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        SpawnData.spawnLocation["spawn"] = (sender as Player).location
        sender.sendMessage(LangConfig.spawnSetMessage)
        return false
    }
}
