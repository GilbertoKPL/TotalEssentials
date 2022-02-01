package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.PlayerData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.util.MainUtil
import github.gilbertokpl.essentialsk.util.PermissionUtil
import github.okkero.skedule.BukkitDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandNick : CommandCreator {
    override val active: Boolean = MainConfig.nicksActivated
    override val consoleCanUse: Boolean = true
    override val commandName = "nick"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.nick"
    override val minimumSize = 1
    override val maximumSize = 2
    override val commandUsage =
        listOf(
            "P_/nick <NickName>",
            "P_/nick remove",
            "essentialsk.commands.nick.other_/nick <player> <NickName>",
            "essentialsk.commands.nick.other_/nick <player> remove"
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        //check if is 1
        if (args.size == 1 && s is Player) {

            //check if nickname do not contain special
            if (MainUtil.checkSpecialCaracteres(args[0])) {
                s.sendMessage(GeneralLang.generalSpecialCaracteresDisabled)
                return false
            }

            //check length of name
            if (args[0].length > 16) {
                s.sendMessage(GeneralLang.nicksNameLength)
                return false
            }
            val playerCache = PlayerData[s] ?: return false

            if (args[0].lowercase() == "remove" || args[0].lowercase() == "remover") {
                //check if is empty
                if (playerCache.nickCache == "") {
                    s.sendMessage(GeneralLang.nicksNickAlreadyOriginal)
                    return false
                }

                playerCache.delNick()
                s.sendMessage(GeneralLang.nicksNickRemovedSuccess)
                return false
            }

            val toCheck = args[0].replace(Regex("&[0-9,a-f]"), "").lowercase()

            if (MainConfig.nicksBlockedNicks.contains(toCheck)) {
                s.sendMessage(GeneralLang.nicksBlocked)
                return false
            }

            val nick = PermissionUtil.colorPermission(s, args[0])

            CoroutineScope(BukkitDispatcher(async = true)).launch {
                if (playerCache.setNick(nick)) {
                    s.sendMessage(GeneralLang.nicksExist)
                    return@launch
                }

                s.sendMessage(GeneralLang.nicksNickSuccess.replace("%nick%", nick))
            }

            return false
        }

        if (args.size != 2) return true

        //check if nickname do not contain . or - to not bug
        if (MainUtil.checkSpecialCaracteres(args[1])) {
            s.sendMessage(GeneralLang.generalSpecialCaracteresDisabled)
            return false
        }

        //check length of name
        if (args[1].length > 16) {
            s.sendMessage(GeneralLang.kitsNameLength)
            return false
        }

        //check perm
        if (s is Player && !s.hasPermission("essentialsk.commands.nick.other")) {
            s.sendMessage(GeneralLang.generalNotPerm)
            return false
        }

        //check if player exist
        val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(GeneralLang.generalPlayerNotOnline)
            return false
        }

        val playerCache = PlayerData[p] ?: return false

        if (args[1].lowercase() == "remove" || args[0].lowercase() == "remover") {
            //check if is empty
            if (playerCache.nickCache == "") {
                s.sendMessage(GeneralLang.nicksNickAlreadyOriginalOther)
                return false
            }
            playerCache.delNick()
            s.sendMessage(GeneralLang.nicksNickRemovedOtherSuccess)
            p.sendMessage(GeneralLang.nicksNickRemovedOtherPlayerSuccess)
            return false
        }

        val nick = args[1].replace("&", "§")

        playerCache.setNick(nick, true)

        s.sendMessage(GeneralLang.nicksNickOtherSuccess.replace("%nick%", nick))
        p.sendMessage(GeneralLang.nicksNickOtherPlayerSuccess.replace("%nick%", nick))
        return false
    }
}
