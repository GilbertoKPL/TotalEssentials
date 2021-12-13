package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.manager.IInstance
import github.gilbertokpl.essentialsk.util.ReflectUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent

class CommandVanish : ICommand {
    override val consoleCanUse: Boolean = true
    override val permission: String = "essentialsk.commands.vanish"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf(
        "P_/vanish",
        "CP_/vanish <PlayerName>"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty() && s !is Player) {
            return true
        }

        if (args.isEmpty()) {
            if (PlayerData(s.name.lowercase()).switchVanish()) {
                s.sendMessage(GeneralLang.getInstance().vanishSendActive)
            }
            else {
                s.sendMessage(GeneralLang.getInstance().vanishSendDisable)
            }
        }

        //check if player is online
        val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(GeneralLang.getInstance().generalPlayerNotOnline)
            return false
        }

        if (PlayerData(p.name.lowercase()).switchVanish()) {
            s.sendMessage(GeneralLang.getInstance().vanishSendOtherActive)
        }
        else {
            s.sendMessage(GeneralLang.getInstance().vanishSendOtherDisable)
        }

        return false
    }

    fun vanishLoginEvent(e: PlayerJoinEvent) {
        ReflectUtil.getInstance().getPlayers().forEach {
            if (PlayerData(it.name).checkVanish()) {
                e.player.hidePlayer(it)
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