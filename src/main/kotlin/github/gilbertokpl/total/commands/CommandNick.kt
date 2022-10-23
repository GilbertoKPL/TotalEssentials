package github.gilbertokpl.total.commands

import github.gilbertokpl.base.external.command.CommandTarget
import github.gilbertokpl.base.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.PlayerData
import github.gilbertokpl.total.util.MainUtil
import github.gilbertokpl.total.util.PermissionUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandNick : github.gilbertokpl.base.external.command.CommandCreator("nick") {

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

        //check if is 1
        if (args.size == 1 && s is Player) {

            //check if nickname do not contain special
            if (MainUtil.checkSpecialCaracteres(args[0])) {
                s.sendMessage(LangConfig.generalSpecialCaracteresDisabled)
                return false
            }

            //check length of name
            if (args[0].length > 16) {
                s.sendMessage(LangConfig.nicksNameLength)
                return false
            }

            if (args[0].lowercase() == "remove" || args[0].lowercase() == "remover") {
                //check if is empty
                if (PlayerData.nickCache[s] == "") {
                    s.sendMessage(LangConfig.nicksAlreadyOriginal)
                    return false
                }

                PlayerData.nickCache[s] = ""

                s.setDisplayName(s.name)

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

        if (args.size != 2) return true

        //check if nickname do not contain . or - to not bug
        if (MainUtil.checkSpecialCaracteres(args[1])) {
            s.sendMessage(LangConfig.generalSpecialCaracteresDisabled)
            return false
        }

        //check length of name
        if (args[1].length > 16) {
            s.sendMessage(LangConfig.kitsNameLength)
            return false
        }

        //check perm
        if (s is Player && !s.hasPermission("totalessentials.commands.nick.other")) {
            s.sendMessage(LangConfig.generalNotPerm)
            return false
        }

        //check if player exist
        val p = github.gilbertokpl.total.TotalEssentials.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        if (args[1].lowercase() == "remove" || args[0].lowercase() == "remover") {
            //check if is empty
            if (PlayerData.nickCache[p] == "") {
                s.sendMessage(LangConfig.nicksAlreadyOriginalOther)
                return false
            }
            PlayerData.nickCache[p] = ""

            p.setDisplayName(p.name)

            s.sendMessage(LangConfig.nicksRemovedOtherSuccess)
            p.sendMessage(LangConfig.nicksRemovedOtherPlayerSuccess)
            return false
        }

        val nick = args[1].replace("&", "§")

        setNick(nick, p, true)

        s.sendMessage(LangConfig.nickOtherSuccess.replace("%nick%", nick))
        p.sendMessage(
            LangConfig.nicksOtherPlayerSuccess.replace(
                "%nick%",
                nick
            )
        )
        return false
    }

    private fun setNick(newNick: String, player: Player, other: Boolean = false): Boolean {
        if (!other) {
            val exist = PlayerData.nickCache.getMap().map { it.value }.contains(newNick.lowercase())

            if (!MainConfig.nicksCanPlayerHaveSameNick &&
                !player.hasPermission("totalessentials.bypass.nickblockednicks") &&
                exist
            ) {
                return true
            }
        }
        player.setDisplayName(newNick)
        PlayerData.nickCache[player] = newNick
        return false
    }
}
