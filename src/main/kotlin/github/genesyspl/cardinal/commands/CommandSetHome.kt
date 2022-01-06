package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.data.`object`.OfflinePlayerData
import github.genesyspl.cardinal.manager.ICommand
import github.genesyspl.cardinal.util.PermissionUtil
import github.genesyspl.cardinal.util.PluginUtil
import github.genesyspl.cardinal.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetHome : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "sethome"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.sethome"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage =
        listOf("/sethome <homeName>", "cardinal.commands.sethome.other_/sethome <playername>:<homeName>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val nameHome = args[0].lowercase()

        //admin
        if (args[0].contains(":") && s.hasPermission("cardinal.commands.sethome.other")) {
            TaskUtil.getInstance().asyncExecutor {
                val split = args[0].split(":")

                val pName = split[0].lowercase()

                val otherPlayerInstance = OfflinePlayerData(pName)

                if (!otherPlayerInstance.checkSql()) {
                    s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalPlayerNotExist)
                    return@asyncExecutor
                }

                if (split.size < 2) {
                    s.sendMessage(
                        github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesHomeOtherList.replace(
                            "%player%",
                            pName
                        )
                            .replace("%list%", otherPlayerInstance.getHomeList().toString())
                    )
                    return@asyncExecutor
                }

                if (otherPlayerInstance.getHomeList().contains(split[1])) {
                    s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesNameAlreadyExist)
                    return@asyncExecutor
                }

                otherPlayerInstance.setHome(split[1].lowercase(), (s as Player).location)

                s.sendMessage(
                    github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesHomeOtherCreated.replace(
                        "%player%",
                        pName
                    )
                        .replace("%home%", split[1])
                )
            }

            return false
        }

        //check if home name do not contain . or - to not bug
        if (PluginUtil.getInstance().checkSpecialCaracteres(nameHome)) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalSpecialCaracteresDisabled)
            return false
        }

        //check length of home name
        if (nameHome.length > 16) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesNameLength)
            return false
        }

        val playerCache = DataManager.getInstance().playerCacheV2[s.name.lowercase()] ?: return false

        //check if already exist
        if (playerCache.homeCache.contains(nameHome)) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesNameAlreadyExist)
            return false
        }

        //update limit
        if (!s.hasPermission("cardinal.commands.sethome." + playerCache.homeLimitCache)) {
            playerCache.homeLimitCache = PermissionUtil.getInstance().getNumberPermission(
                s as Player,
                "cardinal.commands.sethome.",
                github.genesyspl.cardinal.configs.MainConfig.getInstance().homesDefaultLimitHomes
            )
        }

        //check limit of homes
        if (playerCache.homeCache.size >= playerCache.homeLimitCache && !s.hasPermission("cardinal.bypass.homelimit")) {
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesHomeLimitCreated.replace(
                    "%limit%",
                    playerCache.homeLimitCache.toString()
                )
            )
            return false
        }

        //check if world is blocked
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().homesBlockWorlds.contains((s as Player).world.name.lowercase()) && !s.hasPermission(
                "cardinal.bypass.homeblockedworlds"
            )
        ) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesHomeWorldBlocked)
            return false
        }

        playerCache.setHome(nameHome, s.location)
        s.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesHomeCreated.replace(
                "%home%",
                nameHome
            )
        )

        return false
    }
}