package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.dao.OfflinePlayerDAO
import github.gilbertokpl.essentialsk.data.dao.PlayerDataDAO
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.util.TaskUtil
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

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        //admin
        if (args[0].contains(":") && s.hasPermission("essentialsk.commands.delhome.other")) {
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

                if (!otherPlayerInstance.getHomeList().contains(split[1])) {
                    s.sendMessage(GeneralLang.homesNameDontExist)
                    return@asyncExecutor
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

        val playerInstance = PlayerDataDAO[p] ?: return false

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
