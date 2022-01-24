package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.OfflinePlayerDAO
import github.gilbertokpl.essentialsk.data.dao.PlayerDataDAO
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.util.MainUtil
import github.gilbertokpl.essentialsk.util.PermissionUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetHome : CommandCreator {
    override val active: Boolean = MainConfig.homesActivated
    override val consoleCanUse: Boolean = false
    override val commandName = "sethome"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.sethome"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage =
        listOf(
            "/sethome <homeName>",
            "essentialsk.commands.sethome.other_/sethome <playername>:<homeName>"
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val nameHome = args[0].lowercase()

        //admin
        if (args[0].contains(":") && s.hasPermission("essentialsk.commands.sethome.other")) {
            TaskUtil.asyncExecutor {
                val split = args[0].split(":")

                val pName = split[0].lowercase()

                val otherPlayerInstance = OfflinePlayerDAO(pName)

                if (!otherPlayerInstance.checkSql()) {
                    s.sendMessage(GeneralLang.generalPlayerNotExist)
                    return@asyncExecutor
                }

                if (split.size < 2) {
                    s.sendMessage(
                        GeneralLang.homesHomeOtherList.replace("%player%", pName)
                            .replace("%list%", otherPlayerInstance.getHomeList().toString())
                    )
                    return@asyncExecutor
                }

                if (otherPlayerInstance.getHomeList().contains(split[1])) {
                    s.sendMessage(GeneralLang.homesNameAlreadyExist)
                    return@asyncExecutor
                }

                otherPlayerInstance.setHome(split[1].lowercase(), (s as Player).location)

                s.sendMessage(
                    GeneralLang.homesHomeOtherCreated.replace("%player%", pName)
                        .replace("%home%", split[1])
                )
            }

            return false
        }

        //check if home name do not contain . or - to not bug
        if (MainUtil.checkSpecialCaracteres(nameHome)) {
            s.sendMessage(GeneralLang.generalSpecialCaracteresDisabled)
            return false
        }

        //check length of home name
        if (nameHome.length > 16) {
            s.sendMessage(GeneralLang.homesNameLength)
            return false
        }

        val playerCache = PlayerDataDAO[s as Player] ?: return false

        //check if already exist
        if (playerCache.homeCache.contains(nameHome)) {
            s.sendMessage(GeneralLang.homesNameAlreadyExist)
            return false
        }

        //update limit
        if (!s.hasPermission("essentialsk.commands.sethome." + playerCache.homeLimitCache)) {
            playerCache.homeLimitCache = PermissionUtil.getNumberPermission(
                s,
                "essentialsk.commands.sethome.",
                MainConfig.homesDefaultLimitHomes
            )
        }

        //check limit of homes
        if (playerCache.homeCache.size >= playerCache.homeLimitCache &&
            !s.hasPermission("essentialsk.bypass.homelimit")
        ) {
            s.sendMessage(
                GeneralLang.homesHomeLimitCreated.replace(
                    "%limit%",
                    playerCache.homeLimitCache.toString()
                )
            )
            return false
        }

        //check if world is blocked
        if (MainConfig.homesBlockWorlds.contains(s.world.name.lowercase()) && !s.hasPermission(
                "essentialsk.bypass.homeblockedworlds"
            )
        ) {
            s.sendMessage(GeneralLang.homesHomeWorldBlocked)
            return false
        }

        playerCache.setHome(nameHome, s.location)
        s.sendMessage(GeneralLang.homesHomeCreated.replace("%home%", nameHome))

        return false
    }
}
