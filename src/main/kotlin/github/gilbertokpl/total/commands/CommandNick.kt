package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.ServerUtil
import github.gilbertokpl.total.util.PermissionUtil
import github.gilbertokpl.total.util.PlayerUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandNick : CommandCreator("nick") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("nome"),
            active = MainConfig.nicksActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.nick",
            minimumSize = 1,
            maximumSize = 2,
            usage = listOf(
                "P_/nick <NickName>",
                "P_/nick remove",
                "totalessentials.commands.nick.other_/nick <player> <NickName>",
                "totalessentials.commands.nick.other_/nick <player> remove"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        // single argument (self nick)
        if (args.size == 1 && s is Player) {

            if (ServerUtil.checkSpecialCharacters(args[0])) {
                s.sendMessage(LangConfig.generalSpecialCaracteresDisabled)
                return false
            }

            if (args[0].length > 16) {
                s.sendMessage(LangConfig.nicksNameLength)
                return false
            }

            if (args[0].lowercase() in listOf("remove", "remover")) {
                if (PlayerData.nickCache[s] == "") {
                    s.sendMessage(LangConfig.nicksAlreadyOriginal)
                    return false
                }
                PlayerData.nickCache[s] = ""
                PlayerUtil.setDisplayName(s, s.name)
                s.sendMessage(LangConfig.nicksRemovedSuccess)
                return false
            }

            val toCheck = args[0].replace(Regex("&[0-9,a-f]"), "").lowercase()
            if (MainConfig.nicksBlockedNicks.contains(toCheck)) {
                s.sendMessage(LangConfig.nicksBlocked)
                return false
            }

            val nick = PermissionUtil.colorPermission(s, args[0])
            if (setNick(nick, s)) {
                s.sendMessage(LangConfig.nicksExist)
                return false
            }

            s.sendMessage(LangConfig.nicksSuccess.replace("%nick%", nick))
            return false
        }

        // two arguments (other player)
        if (args.size != 2) return true

        if (ServerUtil.checkSpecialCharacters(args[1])) {
            s.sendMessage(LangConfig.generalSpecialCaracteresDisabled)
            return false
        }

        if (args[1].length > 16) {
            s.sendMessage(LangConfig.kitsNameLength)
            return false
        }

        if (s is Player && !s.hasPermission("totalessentials.commands.nick.other")) {
            s.sendMessage(LangConfig.generalNotPerm)
            return false
        }

        val p = TotalEssentials.getInstance().server.getPlayer(args[0]) ?: run {
            s.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        if (args[1].lowercase() in listOf("remove", "remover")) {
            if (PlayerData.nickCache[p] == "") {
                s.sendMessage(LangConfig.nicksAlreadyOriginalOther)
                return false
            }
            PlayerData.nickCache[p] = ""
            PlayerUtil.setDisplayName(p, p.name)
            s.sendMessage(LangConfig.nicksRemovedOtherSuccess)
            p.sendMessage(LangConfig.nicksRemovedOtherPlayerSuccess)
            return false
        }

        val nick = args[1].replace("&", "§")
        setNick(nick, p, true)

        s.sendMessage(LangConfig.nickOtherSuccess.replace("%nick%", nick))
        p.sendMessage(LangConfig.nicksOtherPlayerSuccess.replace("%nick%", nick))

        return false
    }

    // set nickname
    private fun setNick(newNick: String, player: Player, other: Boolean = false): Boolean {
        if (!other) {
            val exist = PlayerData.nickCache.getMap().map { it.value }.contains(newNick.lowercase())
            if (!MainConfig.nicksCanPlayerHaveSameNick &&
                !player.hasPermission("totalessentials.bypass.nickblockednicks") &&
                exist
            ) return true
        }
        PlayerUtil.setDisplayName(player, newNick)
        PlayerData.nickCache[player] = newNick
        return false
    }
}
