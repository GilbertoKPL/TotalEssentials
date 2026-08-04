package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.cache.data.WarpData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.FancyChat
import github.gilbertokpl.total.util.PlayerUtil
import github.gilbertokpl.total.util.PlayerUtil.teleportSafe
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandWarp : CommandManager("warp") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("warps"),
            active = MainConfig.warpsActivated,
            target = CommandTargetType.ALL,
            countdown = 0,
            permission = "totalessentials.commands.warp",
            minimumSize = 0,
            maximumSize = 2,
            usage = listOf(
                "P_/warp <warpName>",
                "totalessentials.commands.warp.other_/warp <playerName> <warpName>"
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        val p = if (sender is Player) {
            sender
        } else {
            null
        }

        if (args.isEmpty()) {
            val warps = WarpData.getWarpList(p)
            val sentFancyMessage = p != null && FancyChat.sendCommandList(
                p,
                LangConfig.warpsList,
                "%list%",
                warps.map { warp ->
                    FancyChat.Action("§e$warp", "/warp $warp")
                }
            )
            if (!sentFancyMessage) {
                sender.sendMessage(
                    LangConfig.warpsList.replace(
                        "%list%",
                        warps.toString()
                    )
                )
            }
            return false
        }

        if (p == null || (args.size == 2 && p.hasPermission("totalessentials.commands.warp.other"))) {
            val newPlayer = Bukkit.getPlayer(args[0].lowercase()) ?: return true

            val warpName = args[1].lowercase()

            //check if not exist
            if (!WarpData.checkIfWarpExist(warpName)) {
                sender.sendMessage(
                    LangConfig.warpsList.replace(
                        "%list%",
                        WarpData.getWarpList(null).toString()
                    )
                )
                return false
            }

            newPlayer.teleportSafe(WarpData.warpLocation[warpName]!!)

            newPlayer.sendMessage(
                LangConfig.warpsTeleportedOtherMessage.replace(
                    "%warp%",
                    warpName
                )
            )
            sender.sendMessage(
                LangConfig.warpsTeleportedOtherSuccess
                    .replace("%warp%", warpName)
                    .replace("%player%", newPlayer.name.lowercase())
            )

            return false
        }

        //check length of warp name
        if (args[0].length > 16) {
            sender.sendMessage(LangConfig.warpsNameLength)
            return false
        }

        val warpName = args[0].lowercase()

        if (!WarpData.checkIfWarpExist(warpName)) {
            val warps = WarpData.getWarpList(p)
            val sentFancyMessage = FancyChat.sendCommandList(
                p,
                LangConfig.warpsList,
                "%list%",
                warps.map { warp ->
                    FancyChat.Action("§e$warp", "/warp $warp")
                }
            )
            if (!sentFancyMessage) {
                p.sendMessage(
                    LangConfig.warpsList.replace(
                        "%list%",
                        warps.toString()
                    )
                )
            }
            return false
        }


        if (!p.hasPermission("totalessentials.commands.warp.$warpName")) {
            p.sendMessage(LangConfig.generalNotPerm)
            return false
        }

        PlayerUtil.teleportWithDelay(
            p,
            WarpData.warpLocation[warpName]!!,
            MainConfig.warpsTimeToTeleport,
            LangConfig.warpsTeleported.replace("%warp%", warpName),
            warpName
        )

        return false
    }
}
