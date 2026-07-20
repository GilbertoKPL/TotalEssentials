package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.ServerUtil
import github.gilbertokpl.total.util.PermissionUtil
import github.gilbertokpl.total.util.PlayerUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandNick : CommandManager("nick") {

    private val colorCodeRegex = Regex("(?i)&[0-9A-FK-OR]")

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("nome"),
            active = MainConfig.nicksActivated,
            target = CommandTargetType.ALL,
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

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        // single argument (self nick)
        if (args.size == 1 && sender is Player) {
            val plainNick = stripColorCodes(args[0])

            if (ServerUtil.hasSpecialCharacters(plainNick)) {
                sender.sendMessage(LangConfig.generalSpecialCaracteresDisabled)
                return false
            }

            if (plainNick.length > 16) {
                sender.sendMessage(LangConfig.nicksNameLength)
                return false
            }

            if (args[0].lowercase() in listOf("remove", "remover")) {
                if (PlayerData.nickCache[sender] == "") {
                    sender.sendMessage(LangConfig.nicksAlreadyOriginal)
                    return false
                }
                PlayerData.nickCache[sender] = ""
                PlayerUtil.setDisplayName(sender, sender.name)
                sender.sendMessage(LangConfig.nicksRemovedSuccess)
                return false
            }

            val toCheck = plainNick.lowercase()
            if (MainConfig.nicksBlockedNicks.any { it.equals(toCheck, ignoreCase = true) }) {
                sender.sendMessage(LangConfig.nicksBlocked)
                return false
            }

            val nick = PermissionUtil.colorPermission(sender, args[0])
            if (setNick(nick, sender)) {
                sender.sendMessage(LangConfig.nicksExist)
                return false
            }

            sender.sendMessage(LangConfig.nicksSuccess.replace("%nick%", nick))
            return false
        }

        // two arguments (other player)
        if (args.size != 2) return true
        val plainNick = stripColorCodes(args[1])

        if (ServerUtil.hasSpecialCharacters(plainNick)) {
            sender.sendMessage(LangConfig.generalSpecialCaracteresDisabled)
            return false
        }

        if (plainNick.length > 16) {
            sender.sendMessage(LangConfig.kitsNameLength)
            return false
        }

        if (sender is Player && !sender.hasPermission("totalessentials.commands.nick.other")) {
            sender.sendMessage(LangConfig.generalNotPerm)
            return false
        }

        val p = TotalEssentials.getInstance().server.getPlayer(args[0]) ?: run {
            sender.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        if (args[1].lowercase() in listOf("remove", "remover")) {
            if (PlayerData.nickCache[p] == "") {
                sender.sendMessage(LangConfig.nicksAlreadyOriginalOther)
                return false
            }
            PlayerData.nickCache[p] = ""
            PlayerUtil.setDisplayName(p, p.name)
            sender.sendMessage(LangConfig.nicksRemovedOtherSuccess)
            p.sendMessage(LangConfig.nicksRemovedOtherPlayerSuccess)
            return false
        }

        val nick = args[1].replace("&", "§")
        setNick(nick, p, true)

        sender.sendMessage(LangConfig.nickOtherSuccess.replace("%nick%", nick))
        p.sendMessage(LangConfig.nicksOtherPlayerSuccess.replace("%nick%", nick))

        return false
    }

    private fun stripColorCodes(nick: String): String {
        return nick.replace(colorCodeRegex, "")
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
