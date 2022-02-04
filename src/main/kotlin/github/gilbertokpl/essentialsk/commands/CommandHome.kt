package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.player.modify.HomeCache.getHomeList
import github.gilbertokpl.essentialsk.player.modify.HomeCache.getHomeLocation
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHome : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.homesActivated,
            consoleCanUse = false,
            commandName = "home",
            timeCoolDown = null,
            permission = "essentialsk.commands.home",
            minimumSize = 0,
            maximumSize = 1,
            commandUsage = listOf(
                "/home <homeName>",
                "essentialsk.commands.home.other_/home <playername>:<homeName>",
                "essentialsk.commands.home.other_/home <playername>:"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val p = s as Player

        val playerCache = PlayerData[p] ?: return false

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
                val split = args[0].split(":")

                val pName = split[0].lowercase()

            val playerData = PlayerData[pName]

            if (playerData == null) {
                p.sendMessage(GeneralLang.generalPlayerNotExist)
                return false
            }

                if (split.size < 2) {
                    p.sendMessage(
                        GeneralLang.homesHomeOtherList.replace("%player%", pName)
                            .replace("%list%", playerData.getHomeList().toString())
                    )
                    return false
                }

            val loc = playerData.getHomeLocation(split[1]) ?: run {
                p.sendMessage(
                    GeneralLang.homesHomeOtherList.replace("%player%", pName)
                        .replace("%list%", playerData.getHomeList().toString())
                )
                return false
            }

                EssentialsK.instance.server.scheduler.runTask(EssentialsK.instance, Runnable {
                    p.teleport(loc)
                })

                p.sendMessage(
                    GeneralLang.homesTeleportedOther.replace("%home%", split[1].lowercase())
                        .replace("%player%", split[0])
                )

            return false
        }

        val nameHome = args[0].lowercase()

        //check if home don't exist
        if (!playerCache.homeCache.contains(nameHome)) {
            p.sendMessage(GeneralLang.homesNameDontExist)
            return false
        }

        val time = MainConfig.homesTimeToTeleport

        if (p.hasPermission("essentialsk.bypass.teleport") || time == 0) {
            p.teleport(playerCache.homeCache[nameHome] ?: return false)
            p.sendMessage(GeneralLang.homesTeleported.replace("%home%", nameHome))
            return false
        }

        if (playerCache.inTeleport) {
            p.sendMessage(GeneralLang.homesInTeleport)
            return false
        }

        val exe = TaskUtil.teleportExecutor(time)

        playerCache.inTeleport = true

        exe {
            playerCache.inTeleport = false
            p.teleport(playerCache.homeCache[nameHome] ?: return@exe)
            p.sendMessage(GeneralLang.homesTeleported.replace("%home%", nameHome))
        }

        p.sendMessage(
            GeneralLang.homesSendTimeToTeleport.replace("%home%", nameHome)
                .replace("%time%", time.toString())
        )
        return false
    }
}
