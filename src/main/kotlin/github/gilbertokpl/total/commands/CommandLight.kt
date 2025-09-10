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
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class CommandLight : CommandManager("light") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("luz"),
            active = MainConfig.lightActivated,
            target = CommandTargetType.ALL,
            countdown = 0,
            permission = "totalessentials.commands.light",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf(
                "P_/light",
                "totalessentials.commands.light.other_/light <playerName>"
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        // if sender is not player and no args, do nothing
        if (args.isEmpty() && sender !is Player) return true

        // admin toggle for other players
        if (args.size == 1) {

            // check permission
            if (sender is Player && !sender.hasPermission("totalessentials.commands.light.other")) {
                sender.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            // check if target player exists
            val p = TotalEssentials.getInstance().server.getPlayer(args[0]) ?: run {
                sender.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            // toggle light
            if (switchLight(p)) {
                p.sendMessage(LangConfig.lightOtherActive)
                sender.sendMessage(LangConfig.lightActivatedOther.replace("%player%", p.name.lowercase()))
            } else {
                p.sendMessage(LangConfig.lightOtherDisable)
                sender.sendMessage(LangConfig.lightDisabledOther.replace("%player%", p.name.lowercase()))
            }

            return false
        }

        // toggle light for self
        if (switchLight(sender as Player)) {
            sender.sendMessage(LangConfig.lightActive)
        } else {
            sender.sendMessage(LangConfig.lightDisable)
        }

        return false
    }

    // toggles night vision effect
    private fun switchLight(player: Player): Boolean {
        val newValue = PlayerData.lightCache[player]?.not() ?: return false
        PlayerData.lightCache[player] = newValue

        if (newValue) {
            player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, Int.MAX_VALUE, 1))
        } else {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION)
        }

        return newValue
    }
}
