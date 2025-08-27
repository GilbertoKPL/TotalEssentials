package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.core.internal.task.dispatcher
import github.gilbertokpl.total.TotalEssentialsJava
import github.gilbertokpl.total.cache.internal.DataTeleport
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.FoliaUtil.teleportSafe
import kotlinx.coroutines.withContext
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpaccept : github.gilbertokpl.core.external.command.CommandCreator("tpaccept") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("back"),
            active = MainConfig.tpaActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.tpa",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf("/tpaccept")
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        val player = sender

        val tpaPlayer = DataTeleport.getTpa(player) ?: run {
            player.sendMessage(LangConfig.tpaNotAnyRequest)
            return false
        }

        val target = TotalEssentialsJava.getInstance().server.getPlayer(tpaPlayer.name) ?: run {
            player.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        // Mensagens de aceitação
        player.sendMessage(LangConfig.tpaRequestAccepted.replace("%player%", target.name))
        val tpaCache = DataTeleport[target] ?: return false
        tpaCache.otherPlayer = null
        tpaCache.wait = false

        // Teleporte imediato se bypass
        if (target.hasPermission("totalessentials.bypass.teleport")) {
            DataTeleport.remove(target)
            target.sendMessage(LangConfig.tpaRequestOtherNoDelayAccepted.replace("%player%", player.name))
            target.teleportSafe(player.location)
            return false
        }

        val time = MainConfig.tpaTimeToTeleport
        target.sendMessage(LangConfig.tpaRequestOtherAccepted.replace("%player%", player.name).replace("%time%", time.toString()))

        val task = TotalEssentialsJava.getBasePlugin().getTask()
        task.async {
            task.waitSeconds(time.toLong() * 50L)
            try {
                withContext(basePlugin!!.plugin.dispatcher(async = false)) {
                    DataTeleport.remove(target)
                    target.teleportSafe(player.location)
                }
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }

        return false
    }
}
