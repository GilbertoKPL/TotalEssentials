package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.data.`object`.OfflinePlayerData
import github.genesyspl.cardinal.manager.ICommand
import github.genesyspl.cardinal.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHome : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "home"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.home"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf(
        "/home <homeName>",
        "cardinal.commands.home.other_/home <playername>:<homeName>",
        "cardinal.commands.home.other_/home <playername>:"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val playerCache = DataManager.getInstance().playerCacheV2[s.name.lowercase()] ?: return false

        if (args.isEmpty()) {
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesHomeList.replace(
                    "%list%",
                    playerCache.homeCache.map { it.key }.toString()
                )
            )
            return false
        }

        //admin
        if (args[0].contains(":") && s.hasPermission("cardinal.commands.home.other")) {
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

                val loc = otherPlayerInstance.getHomeLocation(split[1]) ?: run {
                    TaskUtil.getInstance().asyncExecutor {
                        s.sendMessage(
                            github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesHomeOtherList.replace(
                                "%player%",
                                pName
                            )
                                .replace("%list%", otherPlayerInstance.getHomeList().toString())
                        )
                    }
                    return@asyncExecutor
                }

                github.genesyspl.cardinal.Cardinal.instance.server.scheduler.runTask(
                    github.genesyspl.cardinal.Cardinal.instance,
                    Runnable {
                        (s as Player).teleport(loc)
                    })

                s.sendMessage(
                    github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesTeleportedOther.replace(
                        "%home%",
                        split[1].lowercase()
                    )
                        .replace("%player%", split[0])
                )

            }
            return false
        }

        if (DataManager.getInstance().inTeleport.contains(s)) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesInTeleport)
            return false
        }

        val nameHome = args[0].lowercase()

        //check if home don't exist
        if (!playerCache.homeCache.contains(nameHome)) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesNameDontExist)
            return false
        }

        if (s.hasPermission("cardinal.bypass.teleport")) {
            (s as Player).teleport(playerCache.homeCache[nameHome]!!)
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesTeleported.replace(
                    "%home%",
                    nameHome
                )
            )
            return false
        }

        val time = github.genesyspl.cardinal.configs.MainConfig.getInstance().homesTimeToTeleport

        val exe = TaskUtil.getInstance().teleportExecutor(time)

        DataManager.getInstance().inTeleport.add((s as Player))

        exe {
            DataManager.getInstance().inTeleport.remove(s)
            s.teleport(playerCache.homeCache[nameHome]!!)
            s.sendMessage(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesTeleported.replace(
                    "%home%",
                    nameHome
                )
            )
        }

        s.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesSendTimeToTeleport.replace(
                "%home%",
                nameHome
            )
                .replace("%time%", time.toString())
        )
        return false
    }
}