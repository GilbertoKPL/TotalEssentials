package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.data.`object`.SpawnData
import github.genesyspl.cardinal.manager.ICommand
import github.genesyspl.cardinal.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSpawn : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "spawn"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.spawn"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf("P_/spawn", "cardinal.commands.spawn.other_/spawn <playerName>")
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        val data = SpawnData("spawn")

        if (data.checkCache()) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().spawnSendNotSet)
            return false
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("cardinal.commands.spawn.other")) {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalNotPerm)
                return false
            }

            //check if player exist
            val p = github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalPlayerNotOnline)
                return false
            }

            p.teleport(data.getLocation())
            p.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().spawnSendOtherMessage)
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().spawnSendSuccessOtherMessage.replace(
                    "%player%",
                    p.name
                )
            )

            return false
        }

        if (DataManager.getInstance().inTeleport.contains(s)) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().spawnSendInTeleport)
            return false
        }

        if (s.hasPermission("cardinal.bypass.teleport")) {
            (s as Player).teleport(data.getLocation())
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().spawnSendMessage)
            return false
        }

        val time = github.genesyspl.cardinal.configs.MainConfig.getInstance().spawnTimeToTeleport

        s.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().spawnSendTimeToTeleport.replace(
                "%time%",
                time.toString()
            )
        )

        val exe = TaskUtil.getInstance().teleportExecutor(time)

        DataManager.getInstance().inTeleport.add((s as Player))

        exe {
            DataManager.getInstance().inTeleport.remove(s)
            s.teleport(data.getLocation())
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().spawnSendMessage)
        }

        return false
    }
}