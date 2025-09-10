package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandFly : CommandManager("fly") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("voar"),
            active = MainConfig.flyActivated,
            target = CommandTargetType.ALL,
            countdown = 0,
            permission = "totalessentials.commands.fly",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf(
                "P_/fly",
                "totalessentials.commands.fly.other_/fly <PlayerName>"
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        val senderPlayer = sender as? Player

        // --------------------------------------------------------
        // Fly another player
        // --------------------------------------------------------
        if (args.size == 1) {
            if (senderPlayer != null && !senderPlayer.hasPermission("totalessentials.commands.fly.other")) {
                senderPlayer.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            val target = TotalEssentials.getInstance().server.getPlayer(args[0]) ?: run {
                sender.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            val enabled = switchFly(target)

            if (enabled) {
                target.sendMessage(LangConfig.flyOtherActive)
                sender.sendMessage(LangConfig.flyActivatedOther.replace("%player", target.name))
            } else {
                target.sendMessage(LangConfig.flyOtherDisable)
                sender.sendMessage(LangConfig.flyDisabledOther.replace("%player", target.name))
            }

            return false
        }

        // --------------------------------------------------------
        // Fly self
        // --------------------------------------------------------
        if (senderPlayer == null) return true

        val worldName = senderPlayer.location.world!!.name.lowercase()
        if (MainConfig.flyDisabledWorlds.contains(worldName)) {
            senderPlayer.sendMessage(LangConfig.flyDisabledWorld)
            return false
        }

        if (switchFly(senderPlayer)) {
            senderPlayer.sendMessage(LangConfig.flyActive)
        } else {
            senderPlayer.sendMessage(LangConfig.flyDisable)
        }

        return false
    }

    private fun switchFly(player: Player): Boolean {
        val newValue = PlayerData.flyCache[player]?.not() ?: false
        PlayerData.flyCache[player] = newValue

        if (newValue) {
            player.allowFlight = true
            player.isFlying = true
        } else {
            val gm = PlayerData.gameModeCache[player]
            if (gm != 1 && gm != 3) { // survival/adventure
                player.allowFlight = false
                player.isFlying = false
            }
        }

        return newValue
    }
}
