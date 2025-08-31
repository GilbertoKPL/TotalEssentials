package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class CommandVanish : CommandCreator("vanish") {

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

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args.size == 1) {

            if (sender is Player && !sender.hasPermission("totalessentials.commands.vanish.other")) {
                sender.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            val target = TotalEssentials.getInstance().server.getPlayer(args[0])
            if (target == null) {
                sender.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            val isActive = toggleVanish(target)
            if (isActive) {
                target.sendMessage(LangConfig.vanishOtherActive)
                sender.sendMessage(LangConfig.vanishActivatedOther.replace("%player%", target.name))
            } else {
                target.sendMessage(LangConfig.vanishOtherDisable)
                sender.sendMessage(LangConfig.vanishDisabledOther.replace("%player%", target.name))
            }

            return false
        }

        if (sender !is Player) return true

        val isActive = toggleVanish(sender)
        sender.sendMessage(if (isActive) LangConfig.vanishActive else LangConfig.vanishDisable)
        return false
    }

    private fun toggleVanish(player: Player): Boolean {
        val current = PlayerData.vanishCache[player] ?: false
        val newValue = !current
        PlayerData.vanishCache[player] = newValue

        if (newValue) {
            player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1))
            TotalEssentials.getCore().getReflection().getPlayers().forEach {
                if (!it.hasPermission("totalessentials.commands.vanish") &&
                    !it.hasPermission("totalessentials.bypass.vanish")) {
                    it.hidePlayer(player)
                }
            }
        } else {
            player.removePotionEffect(PotionEffectType.INVISIBILITY)
            TotalEssentials.getCore().getReflection().getPlayers().forEach {
                it.showPlayer(player)
            }
        }

        return newValue
    }
}
