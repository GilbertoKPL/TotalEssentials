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

class CommandVanish : github.gilbertokpl.core.external.command.CommandCreator("vanish") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("v"),
            active = MainConfig.vanishActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.vanish",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf(
                "P_/vanish",
                "totalessentials.commands.vanish.other_/vanish <PlayerName>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("totalessentials.commands.vanish.other")) {
                s.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            //check if player is online
            val p = github.gilbertokpl.total.TotalEssentialsJava.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            if (switchVanish(p)) {
                p.sendMessage(LangConfig.vanishOtherActive)
                s.sendMessage(
                    LangConfig.vanishActivatedOther.replace(
                        "%player%",
                        p.name
                    )
                )
            } else {
                p.sendMessage(LangConfig.vanishOtherDisable)
                s.sendMessage(
                    LangConfig.vanishDisabledOther.replace(
                        "%player%",
                        p.name
                    )
                )
            }

            return false
        }

        if (switchVanish(s as Player)) {
            s.sendMessage(LangConfig.vanishActive)
        } else {
            s.sendMessage(LangConfig.vanishDisable)
        }
        return false
    }

    private fun switchVanish(player: Player): Boolean {

        val newValue = PlayerData.vanishCache[player]!!.not()

        PlayerData.vanishCache[player] = newValue

        if (newValue) {
            player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1))
            github.gilbertokpl.total.TotalEssentialsJava.basePlugin.getReflection().getPlayers().forEach {
                @Suppress("DEPRECATION")
                if (!it.hasPermission("totalessentials.commands.vanish") &&
                    !it.hasPermission("totalessentials.bypass.vanish")
                ) {
                    it.hidePlayer(player)
                }
            }
        } else {
            player.removePotionEffect(PotionEffectType.INVISIBILITY)
            github.gilbertokpl.total.TotalEssentialsJava.basePlugin.getReflection().getPlayers().forEach {
                @Suppress("DEPRECATION")
                it.showPlayer(player)
            }
        }

        return newValue
    }
}
