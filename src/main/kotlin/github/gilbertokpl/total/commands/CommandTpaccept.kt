package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.internal.DataTeleport
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PlayerUtil.teleportSafe
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpaccept : CommandManager("tpaccept") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf(),
            active = MainConfig.tpaActivated,
            target = CommandTargetType.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.tpa",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf("/tpaccept")
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        val tpaPlayer = DataTeleport.getTpa(sender) ?: run {
            sender.sendMessage(LangConfig.tpaNotAnyRequest)
            return false
        }

        val target = TotalEssentials.getInstance().server.getPlayer(tpaPlayer.name) ?: run {
            sender.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        // Mensagens de aceitação
        sender.sendMessage(LangConfig.tpaRequestAccepted.replace("%player%", target.name))
        val tpaCache = DataTeleport[target] ?: return false
        tpaCache.otherPlayer = null
        tpaCache.wait = false

        // Teleporte imediato se bypass
        if (target.hasPermission("totalessentials.bypass.teleport")) {
            DataTeleport.remove(target)
            target.sendMessage(LangConfig.tpaRequestOtherNoDelayAccepted.replace("%player%", sender.name))
            target.teleportSafe(sender.location)
            return false
        }

        val time = MainConfig.tpaTimeToTeleport
        target.sendMessage(LangConfig.tpaRequestOtherAccepted.replace("%player%", sender.name).replace("%time%", time.toString()))

        val task = TotalEssentials.getCore().getTask()
        task.supplyLater(time.toLong()) {
            try {
                DataTeleport.remove(target)
                task.sync {
                    target.teleportSafe(sender.location)
                }
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }

        return false
    }
}
