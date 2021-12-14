package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.OtherConfig
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.manager.IInstance
import github.gilbertokpl.essentialsk.util.ReflectUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerJoinEvent

class CommandVanish : ICommand {
    override val consoleCanUse: Boolean = true
    override val permission: String = "essentialsk.commands.vanish"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf(
        "P_/vanish",
        "essentialsk.commands.vanish.other_/vanish <PlayerName>"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.size == 1) {

            //check perms
            if (s is Player && s.hasPermission("essentialsk.commands.vanish.other")) {
                s.sendMessage(GeneralLang.getInstance().generalNotPerm)
                return false
            }

            //check if player is online
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(GeneralLang.getInstance().generalPlayerNotOnline)
                return false
            }

            if (PlayerData(p.name.lowercase()).switchVanish()) {
                p.sendMessage(GeneralLang.getInstance().vanishSendOtherActive)
                s.sendMessage(GeneralLang.getInstance().vanishSendActivatedOther)
            } else {
                p.sendMessage(GeneralLang.getInstance().vanishSendOtherDisable)
                s.sendMessage(GeneralLang.getInstance().vanishSendDisabledOther)
            }

            return false
        }

        if (PlayerData(s.name.lowercase()).switchVanish()) {
            s.sendMessage(GeneralLang.getInstance().vanishSendActive)
        } else {
            s.sendMessage(GeneralLang.getInstance().vanishSendDisable)
        }
        return false
    }

    fun vanishLoginEvent(e: PlayerJoinEvent) {
        if (e.player.hasPermission("essentialsk.commands.vanish") || e.player.hasPermission("essentialsk.bypass.vanish")) return
        ReflectUtil.getInstance().getPlayers().forEach {
            if (PlayerData(it.name).checkVanish()) {
                e.player.hidePlayer(it)
            }
        }
    }

    fun vanishPreCommandEvent(e: PlayerCommandPreprocessEvent, split: List<String>) {
        if (split.isEmpty()) {
            return
        }
        OtherConfig.getInstance().vanishBlockedOtherCmds.also {
            if (it.containsKey(split[0])) {
                val to = it[split[0]]
                if (split.size >= to!!) {
                    val p = EssentialsK.instance.server.getPlayer(split[to]) ?: return
                    if (PlayerData(p.name).checkVanish()) {
                        e.isCancelled = true
                    }
                }
            }
        }
    }

    companion object : IInstance<CommandVanish> {
        private val instance = createInstance()
        override fun createInstance(): CommandVanish = CommandVanish()
        override fun getInstance(): CommandVanish {
            return instance
        }
    }
}