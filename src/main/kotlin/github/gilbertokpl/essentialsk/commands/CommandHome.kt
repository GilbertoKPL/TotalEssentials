package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.objects.OfflinePlayerData
import github.gilbertokpl.essentialsk.data.objects.PlayerDataV2
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHome : CommandCreator {
    override val consoleCanUse: Boolean = false
    override val commandName = "home"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.home"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf(
        "/home <homeName>",
        "essentialsk.commands.home.other_/home <playername>:<homeName>",
        "essentialsk.commands.home.other_/home <playername>:"
    )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val p = s as Player

        val playerCache = PlayerDataV2[p] ?: return false

        if (args.isEmpty()) {
            p.sendMessage(
                GeneralLang.homesHomeList.replace(
                    "%list%",
                    playerCache.homeCache.map { it.key }.toString()
                )
            )
            return false
        }

        //admin
        if (args[0].contains(":") && p.hasPermission("essentialsk.commands.home.other")) {
            TaskUtil.asyncExecutor {
                val split = args[0].split(":")

                val pName = split[0].lowercase()

                val otherPlayerInstance = OfflinePlayerData(pName)

                if (!otherPlayerInstance.checkSql()) {
                    p.sendMessage(GeneralLang.generalPlayerNotExist)
                    return@asyncExecutor
                }

                if (split.size < 2) {
                    p.sendMessage(
                        GeneralLang.homesHomeOtherList.replace("%player%", pName)
                            .replace("%list%", otherPlayerInstance.getHomeList().toString())
                    )
                    return@asyncExecutor
                }

                val loc = otherPlayerInstance.getHomeLocation(split[1]) ?: run {
                    TaskUtil.asyncExecutor {
                        p.sendMessage(
                            GeneralLang.homesHomeOtherList.replace("%player%", pName)
                                .replace("%list%", otherPlayerInstance.getHomeList().toString())
                        )
                    }
                    return@asyncExecutor
                }

                EssentialsK.instance.server.scheduler.runTask(EssentialsK.instance, Runnable {
                    p.teleport(loc)
                })

                p.sendMessage(
                    GeneralLang.homesTeleportedOther.replace("%home%", split[1].lowercase())
                        .replace("%player%", split[0])
                )

            }
            return false
        }

        if (DataManager.inTeleport.contains(s)) {
            p.sendMessage(GeneralLang.homesInTeleport)
            return false
        }

        val nameHome = args[0].lowercase()

        //check if home don't exist
        if (!playerCache.homeCache.contains(nameHome)) {
            p.sendMessage(GeneralLang.homesNameDontExist)
            return false
        }

        if (p.hasPermission("essentialsk.bypass.teleport")) {
            p.teleport(playerCache.homeCache[nameHome]!!)
            p.sendMessage(GeneralLang.homesTeleported.replace("%home%", nameHome))
            return false
        }

        val time = MainConfig.homesTimeToTeleport

        val exe = TaskUtil.teleportExecutor(time)

        DataManager.inTeleport.add(p)

        exe {
            DataManager.inTeleport.remove(s)
            p.teleport(playerCache.homeCache[nameHome]!!)
            p.sendMessage(GeneralLang.homesTeleported.replace("%home%", nameHome))
        }

        p.sendMessage(
            GeneralLang.homesSendTimeToTeleport.replace("%home%", nameHome)
                .replace("%time%", time.toString())
        )
        return false
    }
}
