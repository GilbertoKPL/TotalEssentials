package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.data.OfflinePlayerData
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHome : ICommand {
    override val consoleCanUse: Boolean = false
    override val permission: String = "essentialsk.commands.home"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf(
        "/home (homeName)",
        "essentialsk.commands.home.other_/home (playername):(homeName)",
        "essentialsk.commands.home.other_/home (playername):"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val playerInstance = PlayerData(s as Player)

        val playerCache = playerInstance.getCache() ?: return false

        if (args.isEmpty()) {
            s.sendMessage(
                GeneralLang.getInstance().homesHomeList.replace(
                    "%list%",
                    playerCache.homeCache.map { it.key }.toString()
                )
            )
            return false
        }

        //admin
        if (args[0].contains(":") && s.hasPermission("essentialsk.commands.home.other")) {
            val split = args[0].split(":")

            val pName = split[0].lowercase()

            val p = EssentialsK.instance.server.getOfflinePlayer(pName)

            val otherPlayerInstance = OfflinePlayerData(p)

            if (!otherPlayerInstance.checkExist().get()) {
                s.sendMessage(GeneralLang.getInstance().generalPlayerNotExist)
                return false
            }

            if (split.size < 2) {
                s.sendMessage(
                    GeneralLang.getInstance().homesHomeOtherList.replace("%player%", pName)
                        .replace("%list%", otherPlayerInstance.getHomeList().get().toString())
                )
                return false
            }

            val loc = otherPlayerInstance.getLocationOfHome(split[1]).get() ?: run {
                s.sendMessage(
                    GeneralLang.getInstance().homesHomeOtherList.replace("%player%", pName)
                        .replace("%list%", otherPlayerInstance.getHomeList().get().toString())
                )
                return false
            }

            s.teleport(loc)

            s.sendMessage(
                GeneralLang.getInstance().homesTeleportedOther.replace("%home%", split[1].lowercase())
                    .replace("%player%", split[0])
            )


            return false
        }

        if (Dao.getInstance().inTeleport.contains(s)) {
            s.sendMessage(GeneralLang.getInstance().homesInTeleport)
            return false
        }

        val nameHome = args[0].lowercase()

        //check if home don't exist
        if (!playerCache.homeCache.contains(nameHome)) {
            s.sendMessage(GeneralLang.getInstance().homesNameDontExist)
            return false
        }

        if (s.hasPermission("essentialsk.bypass.teleport")) {
            s.teleport(playerCache.homeCache[nameHome]!!)
            s.sendMessage(GeneralLang.getInstance().homesTeleported.replace("%home%", nameHome))
            return false
        }

        val time = MainConfig.getInstance().homesTimeToTeleport

        Dao.getInstance().inTeleport.add(s)

        val exe = TaskUtil.getInstance().teleportExecutor(time)

        exe {
            Dao.getInstance().inTeleport.remove(s)
            s.teleport(playerCache.homeCache[nameHome]!!)
            s.sendMessage(GeneralLang.getInstance().homesTeleported.replace("%home%", nameHome))
        }

        s.sendMessage(
            GeneralLang.getInstance().homesTimeToTeleport.replace("%home%", nameHome).replace("%time%", time.toString())
        )

        return false
    }
}