package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.data.SpawnData
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class CommandSpawn : ICommand {
    override val consoleCanUse: Boolean = true
    override val permission: String = "essentialsk.commands.spawn"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf("P_/spawn", "essentialsk.commands.spawn.other_/spawn <playerName>")
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        val data = SpawnData("spawn")

        if (data.checkCache()) {
            s.sendMessage(GeneralLang.getInstance().spawnSendNotSet)
            return false
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("essentialsk.commands.spawn.other")) {
                s.sendMessage(GeneralLang.getInstance().generalNotPerm)
                return false
            }

            //check if player exist
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(GeneralLang.getInstance().generalPlayerNotOnline)
                return false
            }

            p.teleport(data.getLocation())
            p.sendMessage(GeneralLang.getInstance().spawnSendOtherMessage)
            s.sendMessage(GeneralLang.getInstance().spawnSendSucessOtherMessage.replace("%player%", p.name))

            return false
        }

        if (Dao.getInstance().inTeleport.contains(s)) {
            s.sendMessage(GeneralLang.getInstance().spawnSendInTeleport)
            return false
        }

        if (s.hasPermission("essentialsk.bypass.teleport")) {
            (s as Player).teleport(data.getLocation())
            s.sendMessage(GeneralLang.getInstance().spawnSendMessage)
            return false
        }

        val time = MainConfig.getInstance().spawnTimeToTeleport

        s.sendMessage(GeneralLang.getInstance().spawnSendTimeToTeleport.replace("%time%", time.toString()))

        val exe = TaskUtil.getInstance().teleportExecutor(time)

        Dao.getInstance().inTeleport.add((s as Player))

        exe {
            Dao.getInstance().inTeleport.remove(s)
            s.teleport(data.getLocation())
            s.sendMessage(GeneralLang.getInstance().spawnSendMessage)
        }

        return false
    }
}