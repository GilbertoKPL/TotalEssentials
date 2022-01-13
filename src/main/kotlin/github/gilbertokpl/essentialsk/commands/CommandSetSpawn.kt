package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.objects.SpawnDataV2
import github.gilbertokpl.essentialsk.manager.CommandCreator
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetSpawn : CommandCreator {
    override val active: Boolean = MainConfig.spawnActivated
    override val consoleCanUse: Boolean = false
    override val commandName = "setspawn"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.setspawn"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf("/setspawn")

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        SpawnDataV2.set("spawn", (s as Player).location, s)
        return false
    }
}
