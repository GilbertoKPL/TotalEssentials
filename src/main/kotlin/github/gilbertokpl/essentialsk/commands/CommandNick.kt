package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.player.modify.NickCache.delNick
import github.gilbertokpl.essentialsk.player.modify.NickCache.setNick
import github.gilbertokpl.essentialsk.util.MainUtil
import github.gilbertokpl.essentialsk.util.PermissionUtil
import github.okkero.skedule.BukkitDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandNick : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.nicksActivated,
            consoleCanUse = true,
            commandName = "nick",
            timeCoolDown = null,
            permission = "essentialsk.commands.nick",
            minimumSize = 1,
            maximumSize = 2,
            commandUsage = listOf(
                "P_/nick <NickName>",
                "P_/nick remove",
                "essentialsk.commands.nick.other_/nick <player> <NickName>",
                "essentialsk.commands.nick.other_/nick <player> remove"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

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
            val playerCache = PlayerData[s] ?: return false

            if (args[0].lowercase() == "remove" || args[0].lowercase() == "remover") {
                //check if is empty
                if (playerCache.nickCache == "") {
                    s.sendMessage(LangConfig.nicksAlreadyOriginal)
                    return false
                }

                playerCache.delNick(s)
                s.sendMessage(LangConfig.nicksRemovedSuccess)
                return false
            }

            val toCheck = args[0].replace(Regex("&[0-9,a-f]"), "").lowercase()

            if (MainConfig.nicksBlockedNicks.contains(toCheck)) {
                s.sendMessage(LangConfig.nicksBlocked)
                return false
            }

            val nick = PermissionUtil.colorPermission(s, args[0])

            CoroutineScope(BukkitDispatcher(async = true)).launch {
                if (playerCache.setNick(nick, s)) {
                    s.sendMessage(LangConfig.nicksExist)
                    return@launch
                }

                s.sendMessage(LangConfig.nicksSuccess.replace("%nick%", nick))
            }

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
        if (s is Player && !s.hasPermission("essentialsk.commands.nick.other")) {
            s.sendMessage(LangConfig.generalNotPerm)
            return false
        }

        //check if player exist
        val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        val playerCache = PlayerData[p] ?: return false

        if (args[1].lowercase() == "remove" || args[0].lowercase() == "remover") {
            //check if is empty
            if (playerCache.nickCache == "") {
                s.sendMessage(LangConfig.nicksAlreadyOriginalOther)
                return false
            }
            playerCache.delNick(p)
            s.sendMessage(LangConfig.nicksRemovedOtherSuccess)
            p.sendMessage(LangConfig.nicksRemovedOtherPlayerSuccess)
            return false
        }

        val nick = args[1].replace("&", "§")

        playerCache.setNick(nick, p, true)

        s.sendMessage(LangConfig.nickOtherSuccess.replace("%nick%", nick))
        p.sendMessage(LangConfig.nicksOtherPlayerSuccess.replace("%nick%", nick))
        return false
    }
}
