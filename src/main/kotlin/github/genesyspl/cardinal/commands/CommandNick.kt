package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.manager.ICommand
import github.genesyspl.cardinal.util.PermissionUtil
import github.genesyspl.cardinal.util.PluginUtil
import github.genesyspl.cardinal.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandNick : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "nick"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.nick"
    override val minimumSize = 1
    override val maximumSize = 2
    override val commandUsage = listOf(
        "/nick <NickName>",
        "/nick remove",
        "cardinal.commands.nick.other_/nick <player> <NickName>",
        "cardinal.commands.nick.other_/nick <player> remove"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        //check if is 1
        if (args.size == 1 && s is Player) {

            //check if nickname do not contain special
            if (PluginUtil.getInstance().checkSpecialCaracteres(args[0])) {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalSpecialCaracteresDisabled)
                return false
            }

            //check length of name
            if (args[0].length > 16) {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().nicksNameLength)
                return false
            }
            val playerCache = DataManager.getInstance().playerCacheV2[s.name.lowercase()] ?: return false

            if (args[0].lowercase() == "remove" || args[0].lowercase() == "remover") {
                //check if is empty
                if (playerCache.fakeNickCache == "") {
                    s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().nicksNickAlreadyOriginal)
                    return false
                }

                playerCache.delNick()
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().nicksNickRemovedSuccess)
                return false
            }

            val toCheck = args[0].replace(Regex("&[0-9,a-f]"), "").lowercase()

            if (github.genesyspl.cardinal.configs.MainConfig.getInstance().nicksBlockedNicks.contains(toCheck)) {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().nicksBlocked)
                return false
            }

            val nick = PermissionUtil.getInstance().colorPermission(s, args[0])

            TaskUtil.getInstance().asyncExecutor {
                if (playerCache.setNick(nick)) {
                    s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().nicksExist)
                    return@asyncExecutor
                }

                s.sendMessage(
                    github.genesyspl.cardinal.configs.GeneralLang.getInstance().nicksNickSuccess.replace(
                        "%nick%",
                        nick
                    )
                )
            }

            return false
        }

        if (args.size != 2) return true

        //check if nickname do not contain . or - to not bug
        if (PluginUtil.getInstance().checkSpecialCaracteres(args[1])) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalSpecialCaracteresDisabled)
            return false
        }

        //check length of name
        if (args[1].length > 16) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsNameLength)
            return false
        }

        //check perm
        if (s is Player && !s.hasPermission("cardinal.commands.nick.other")) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalNotPerm)
            return false
        }

        //check if player exist
        val p = github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalPlayerNotOnline)
            return false
        }

        val playerCache = DataManager.getInstance().playerCacheV2[p.name.lowercase()] ?: return false

        if (args[1].lowercase() == "remove" || args[0].lowercase() == "remover") {
            //check if is empty
            if (playerCache.fakeNickCache == "") {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().nicksNickAlreadyOriginalOther)
                return false
            }
            playerCache.delNick()
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().nicksNickRemovedOtherSuccess)
            p.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().nicksNickRemovedOtherPlayerSuccess)
            return false
        }

        val nick = args[1].replace("&", "§")

        playerCache.setNick(nick, true)

        s.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().nicksNickOtherSuccess.replace(
                "%nick%",
                nick
            )
        )
        p.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().nicksNickOtherPlayerSuccess.replace(
                "%nick%",
                nick
            )
        )
        return false
    }
}