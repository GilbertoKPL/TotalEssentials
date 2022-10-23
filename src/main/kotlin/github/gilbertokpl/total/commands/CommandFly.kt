package github.gilbertokpl.total.commands

import github.gilbertokpl.base.external.command.CommandTarget
import github.gilbertokpl.base.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.PlayerData
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandFly : github.gilbertokpl.base.external.command.CommandCreator("fly") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("voar"),
            active = MainConfig.flyActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "essentialsk.commands.fly",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf(
                "P_/fly",
                "essentialsk.commands.fly.other_/fly <PlayerName>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        //admin
        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("essentialsk.commands.fly.other")) {
                s.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            //check if player is online
            val p = github.gilbertokpl.total.TotalEssentials.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            if (switchFly(p)) {
                p.sendMessage(LangConfig.flyOtherActive)
                s.sendMessage(
                    LangConfig.flyActivatedOther.replace(
                        "%player",
                        p.name
                    )
                )
            } else {
                p.sendMessage(LangConfig.flyOtherDisable)
                s.sendMessage(
                    LangConfig.flyDisabledOther.replace(
                        "%player",
                        p.name
                    )
                )
            }

            return false
        }

        if (MainConfig.flyDisabledWorlds.contains((s as Player).location.world!!.name.lowercase())) {
            s.sendMessage(LangConfig.flyDisabledWorld)
            return false
        }

        if (switchFly(s)) {
            s.sendMessage(LangConfig.flyActive)
        } else {
            s.sendMessage(LangConfig.flyDisable)
        }
        return false
    }

    private fun switchFly(player: Player): Boolean {
        val newValue = PlayerData.flyCache[player]?.not() ?: return false

        PlayerData.flyCache[player] = newValue

        if (newValue) {
            player.allowFlight = true
            player.isFlying = true
        } else {
            //desbug gamemode
            val gm = PlayerData.gameModeCache[player]
            if (gm != 1 && gm != 3) {
                player.allowFlight = false
                player.isFlying = false
            }
        }

        return newValue
    }
}
