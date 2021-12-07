package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.PluginUtil
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
                //check if is empty
                if ((playerCache.getCache()?.FakeNick ?: return) == "") {
                    s.sendMessage(GeneralLang.getInstance().nicksNickAlreadyOriginal)
                    return
                }
                playerCache.removeNick()
                s.sendMessage(GeneralLang.getInstance().nicksNickRemovedSuccess)
                return
            }

            val toCheck = args[0].replace(Regex("&[0-9,a-f]"), "").lowercase()

            if (MainConfig.getInstance().nicksBlockedNicks.contains(toCheck)) {
                s.sendMessage(GeneralLang.getInstance().nicksBlocked)
                return
            }

            val nick = PluginUtil.getInstance().colorPermission(s, args[0])

            if (playerCache.setNick(nick).get()) {
                s.sendMessage(GeneralLang.getInstance().nicksExist)
            }

            s.sendMessage(GeneralLang.getInstance().nicksNickSuccess.replace("%nick%", nick))

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

        if (args[1].lowercase() == "remove") {
            //check if is empty
            if ((playerCache.getCache()?.FakeNick ?: return) == "") {
                s.sendMessage(GeneralLang.getInstance().nicksNickAlreadyOriginalOther)
                return
            }
            playerCache.removeNick()
            s.sendMessage(GeneralLang.getInstance().nicksNickRemovedOtherSuccess)
            p.sendMessage(GeneralLang.getInstance().nicksNickRemovedOtherPlayerSuccess)
            return
        }

        val nick = args[1].replace("&", "$")

        playerCache.setNick(nick, true)

        s.sendMessage(GeneralLang.getInstance().nicksNickOtherSuccess.replace("%nick%", nick))
        p.sendMessage(GeneralLang.getInstance().nicksNickOtherPlayerSuccess.replace("%nick%", nick))
    }
}