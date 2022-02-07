package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.data.dao.SpawnData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetSpawn : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.spawnActivated,
            consoleCanUse = false,
            commandName = "setspawn",
            timeCoolDown = null,
            permission = "essentialsk.commands.setspawn",
            minimumSize = 0,
            maximumSize = 0,
            commandUsage = listOf("/setspawn")
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        SpawnData.set("spawn", (s as Player).location)
        s.sendMessage(LangConfig.spawnSendSetMessage)
        return false
    }
}
