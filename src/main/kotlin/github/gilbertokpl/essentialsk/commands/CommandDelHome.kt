package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.OfflinePlayer
import github.gilbertokpl.essentialsk.data.dao.PlayerData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.okkero.skedule.BukkitDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandDelHome : CommandCreator {
    override val active: Boolean = MainConfig.homesActivated
    override val consoleCanUse: Boolean = false
    override val commandName = "delhome"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.delhome"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage =
        listOf(
            "/delhome <homeName>",
            "essentialsk.commands.delhome.other_/delhome <playername>:<homeName>"
        )

    @OptIn(DelicateCoroutinesApi::class)
    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        //admin
        if (args[0].contains(":") && s.hasPermission("essentialsk.commands.delhome.other")) {
            CoroutineScope(BukkitDispatcher(async = true)).launch {
                val split = args[0].split(":")

                val pName = split[0].lowercase()

                val otherPlayerInstance = OfflinePlayer(pName)

                if (!otherPlayerInstance.checkSql()) {
                    s.sendMessage(GeneralLang.generalPlayerNotExist)
                    return@launch
                }

                if (split.size < 2) {
                    s.sendMessage(
                        GeneralLang.homesHomeOtherList.replace("%player%", pName)
                            .replace("%list%", otherPlayerInstance.getHomeList().toString())
                    )
                    return@launch
                }

                if (!otherPlayerInstance.getHomeList().contains(split[1])) {
                    s.sendMessage(GeneralLang.homesNameDontExist)
                    return@launch
                }

                otherPlayerInstance.delHome(split[1])

                s.sendMessage(
                    GeneralLang.homesHomeOtherRemoved.replace("%player%", pName)
                        .replace("%home%", split[1])
                )
            }

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
