package github.gilbertokpl.total.commands

import github.gilbertokpl.base.external.command.CommandTarget
import github.gilbertokpl.base.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.SpawnData
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetSpawn : github.gilbertokpl.base.external.command.CommandCreator("setspawn") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(""),
            active = MainConfig.spawnActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "essentialsk.commands.setspawn",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf("/setspawn")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        SpawnData.spawnLocation["spawn"] = (s as Player).location
        s.sendMessage(LangConfig.spawnSetMessage)
        return false
    }
}
