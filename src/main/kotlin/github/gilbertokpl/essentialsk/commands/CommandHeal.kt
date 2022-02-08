package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.util.ReflectUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHeal : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.healActivated,
            consoleCanUse = true,
            commandName = "heal",
            timeCoolDown = null,
            permission = "essentialsk.commands.heal",
            minimumSize = 0,
            maximumSize = 1,
            commandUsage = listOf(
                "P_/heal",
                "essentialsk.commands.heal.other_/heal <playerName>"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && !s.hasPermission("essentialsk.commands.heal.other")) {
                s.sendMessage(LangConfig.generalNotPerm)
                return false
            }

            //check if player exist
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            if (MainConfig.healNeedHealBelow && ReflectUtil.getHealth(p) >= MAX_PLAYER_HEAL) {
                s.sendMessage(LangConfig.healOtherFullMessage)
                return false
            }

            ReflectUtil.setHealth(p, 20)
            p.sendMessage(LangConfig.healOtherMessage)
            s.sendMessage(LangConfig.healSuccessOtherMessage.replace("%player%", p.name))

            return false
        }

        if (MainConfig.healNeedHealBelow && ReflectUtil.getHealth(s as Player) >= MAX_PLAYER_HEAL) {
            s.sendMessage(LangConfig.healFullMessage)
            return false
        }

        ReflectUtil.setHealth(s as Player, MAX_PLAYER_HEAL)
        s.sendMessage(LangConfig.healMessage)
        return false
    }

    companion object {
        private const val MAX_PLAYER_HEAL = 20
    }
}
