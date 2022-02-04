package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.player.modify.HomeCache.delHome
import github.gilbertokpl.essentialsk.player.modify.HomeCache.getHomeList
import kotlinx.coroutines.DelicateCoroutinesApi
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandDelHome : CommandCreator {

    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.homesActivated,
            consoleCanUse = false,
            commandName = "delhome",
            timeCoolDown = null,
            permission = "essentialsk.commands.delhome",
            minimumSize = 1,
            maximumSize = 1,
            commandUsage = listOf(
                "/delhome <homeName>",
                "essentialsk.commands.delhome.other_/delhome <playername>:<homeName>"
            )
        )

    @OptIn(DelicateCoroutinesApi::class)
    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        //admin
        if (args[0].contains(":") && s.hasPermission("essentialsk.commands.delhome.other")) {
            val split = args[0].split(":")

            val pName = split[0].lowercase()

            val playerData = PlayerData[pName]

            if (playerData == null) {
                s.sendMessage(GeneralLang.generalPlayerNotExist)
                return false
            }

            if (split.size < 2) {
                s.sendMessage(
                    GeneralLang.homesHomeOtherList.replace("%player%", pName)
                        .replace("%list%", playerData.getHomeList().toString())
                )
                return false
            }

            if (!playerData.getHomeList().contains(split[1])) {
                s.sendMessage(GeneralLang.homesNameDontExist)
                return false
            }

            playerData.delHome(split[1])

            s.sendMessage(
                GeneralLang.homesHomeOtherRemoved.replace("%player%", pName)
                    .replace("%home%", split[1])
            )
            return false
        }

        val p = s as Player

        val nameHome = args[0].lowercase()

        val playerInstance = PlayerData[p] ?: return false

        //check if home don't exist
        if (!playerInstance.homeCache.contains(nameHome)) {
            p.sendMessage(GeneralLang.homesNameDontExist)
            return false
        }

        playerInstance.delHome(nameHome)

        p.sendMessage(GeneralLang.homesHomeRemoved.replace("%home%", nameHome))
        return false
    }
}
