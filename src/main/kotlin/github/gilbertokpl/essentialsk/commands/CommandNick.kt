package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandNick : ICommand {
    override val consoleCanUse: Boolean = true
    override val permission: String = "essentialsk.commands.nick"
    override val minimumSize = 1
    override val maximumSize = 2
    override val commandUsage = "/nick [(NickName) / remove]"
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>) {

        //check if is 1
        if (args.size == 1 && s is Player) {

            //check length of name
            if (args[0].length > 16) {
                s.sendMessage(GeneralLang.getInstance().nicksNameLength)
                return
            }
            val playerCache = PlayerData(s)

            if (args[0].lowercase() == "remove") {
                playerCache.removeNick()
                return
            }

            val toCheck = args[0].replace(Regex("&[0-9,a-f]"), "").lowercase()

            if (MainConfig.getInstance().nicksBlockedNicks.contains(toCheck)) {
                s.sendMessage(GeneralLang.getInstance().nicksBlocked)
                return
            }

            if (playerCache.setNick(args[0]).get()) {
                s.sendMessage(GeneralLang.getInstance().nicksExist)
            }
            return
        }

        if (args.size != 2) return

        //check length of name
        if (args[1].length > 16) {
            s.sendMessage(GeneralLang.getInstance().kitsNameLength)
            return
        }

        //check perm
        if (s is Player && !s.hasPermission("essentialsk.commands.nick.other")) {
            s.sendMessage(GeneralLang.getInstance().generalNotPerm)
            return
        }

        //check if player exist
        val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(GeneralLang.getInstance().generalPlayerNotOnline)
            return
        }

        val playerCache = PlayerData(p)

        playerCache.setNick(args[1], true)
    }
}