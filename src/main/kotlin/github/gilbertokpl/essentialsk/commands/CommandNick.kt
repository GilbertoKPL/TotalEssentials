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
    override val commandUsage = listOf(
        "/nick (NickName)",
        "/nick remove",
        "essentialsk.commands.nick.other_/nick (player) (NickName)",
        "essentialsk.commands.nick.other_/nick (player) remove"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        //check if is 1
        if (args.size == 1 && s is Player) {

            //check if nickname do not contain . or - to not bug
            if (PluginUtil.getInstance().checkSpecialCaracteres(args[0])) {
                s.sendMessage(GeneralLang.getInstance().generalSpecialCaracteresDisabled)
                return false
            }

            //check length of name
            if (args[0].length > 16) {
                s.sendMessage(GeneralLang.getInstance().nicksNameLength)
                return false
            }
            val playerCache = PlayerData(s.name)

            if (args[0].lowercase() == "remove" || args[0].lowercase() == "remover") {
                //check if is empty
                if ((playerCache.getCache()?.FakeNick ?: return false) == "") {
                    s.sendMessage(GeneralLang.getInstance().nicksNickAlreadyOriginal)
                    return false
                }
                playerCache.removeNick()
                s.sendMessage(GeneralLang.getInstance().nicksNickRemovedSuccess)
                return false
            }

            val toCheck = args[0].replace(Regex("&[0-9,a-f]"), "").lowercase()

            if (MainConfig.getInstance().nicksBlockedNicks.contains(toCheck)) {
                s.sendMessage(GeneralLang.getInstance().nicksBlocked)
                return false
            }

            val nick = PluginUtil.getInstance().colorPermission(s, args[0])

            if (playerCache.setNick(nick).get()) {
                s.sendMessage(GeneralLang.getInstance().nicksExist)
                return false
            }

            s.sendMessage(GeneralLang.getInstance().nicksNickSuccess.replace("%nick%", nick))

            return false
        }

        if (args.size != 2) return true

        //check if nickname do not contain . or - to not bug
        if (PluginUtil.getInstance().checkSpecialCaracteres(args[1])) {
            s.sendMessage(GeneralLang.getInstance().generalSpecialCaracteresDisabled)
            return false
        }

        //check length of name
        if (args[1].length > 16) {
            s.sendMessage(GeneralLang.getInstance().kitsNameLength)
            return false
        }

        //check perm
        if (s is Player && !s.hasPermission("essentialsk.commands.nick.other")) {
            s.sendMessage(GeneralLang.getInstance().generalNotPerm)
            return false
        }

        //check if player exist
        val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(GeneralLang.getInstance().generalPlayerNotOnline)
            return false
        }

        val playerCache = PlayerData(p.name)

        if (args[1].lowercase() == "remove" || args[0].lowercase() == "remover") {
            //check if is empty
            if ((playerCache.getCache()?.FakeNick ?: return false) == "") {
                s.sendMessage(GeneralLang.getInstance().nicksNickAlreadyOriginalOther)
                return false
            }
            playerCache.removeNick()
            s.sendMessage(GeneralLang.getInstance().nicksNickRemovedOtherSuccess)
            p.sendMessage(GeneralLang.getInstance().nicksNickRemovedOtherPlayerSuccess)
            return false
        }

        val nick = args[1].replace("&", "$")

        playerCache.setNick(nick, true)

        s.sendMessage(GeneralLang.getInstance().nicksNickOtherSuccess.replace("%nick%", nick))
        p.sendMessage(GeneralLang.getInstance().nicksNickOtherPlayerSuccess.replace("%nick%", nick))
        return false
    }
}