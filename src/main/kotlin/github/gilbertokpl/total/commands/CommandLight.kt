package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class CommandLight : github.gilbertokpl.core.external.command.CommandCreator("light") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("luz"),
            active = MainConfig.lightActivated,
            target = CommandTarget.ALL,
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

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("totalessentials.commands.light.other")) {
                s.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            //check if player exist
            val p = github.gilbertokpl.total.TotalEssentials.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            if (switchLight(p)) {
                p.sendMessage(LangConfig.lightOtherActive)
                s.sendMessage(
                    LangConfig.lightActivatedOther
                        .replace("%player%", p.name.lowercase())
                )
            } else {
                p.sendMessage(LangConfig.lightOtherDisable)
                s.sendMessage(
                    LangConfig.lightDisabledOther
                        .replace("%player%", p.name.lowercase())
                )
            }

            return false
        }

        if (switchLight(s as Player)) {
            s.sendMessage(LangConfig.lightActive)
        } else {
            s.sendMessage(LangConfig.lightDisable)
        }

        return false
    }

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
