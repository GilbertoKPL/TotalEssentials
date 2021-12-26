package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandDelHome : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "delhome"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.delhome"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage =
        listOf("/delhome <homeName>", "essentialsk.commands.delhome.other_/delhome <playername>:<homeName>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        //admin
        if (args[0].contains(":") && s.hasPermission("essentialsk.commands.delhome.other")) {
            TaskUtil.getInstance().asyncExecutor {
                val split = args[0].split(":")

                val pName = split[0].lowercase()

                val otherPlayerInstance = PlayerData(pName)

                if (!otherPlayerInstance.checkSql()) {
                    s.sendMessage(GeneralLang.getInstance().generalPlayerNotExist)
                    return@asyncExecutor
                }

                if (split.size < 2) {
                    TaskUtil.getInstance().asyncExecutor {
                        s.sendMessage(
                            GeneralLang.getInstance().homesHomeOtherList.replace("%player%", pName)
                                .replace("%list%", otherPlayerInstance.getHomeList().toString())
                        )
                    }
                    return@asyncExecutor
                }

                if (!otherPlayerInstance.getHomeList().contains(split[1])) {
                    s.sendMessage(GeneralLang.getInstance().homesNameDontExist)
                    return@asyncExecutor
                }

                otherPlayerInstance.delHome(split[1])

                s.sendMessage(
                    GeneralLang.getInstance().homesHomeOtherRemoved.replace("%player%", pName)
                        .replace("%home%", split[1])
                )
            }

            return false
        }

        val nameHome = args[0].lowercase()

        val playerInstance = PlayerData(s.name)

        val playerCache = playerInstance.getCache() ?: return false

        //check if home don't exist
        if (!playerCache.homeCache.contains(nameHome)) {
            s.sendMessage(GeneralLang.getInstance().homesNameDontExist)
            return false
        }

        playerInstance.delHome(nameHome)

        s.sendMessage(GeneralLang.getInstance().homesHomeRemoved.replace("%home%", nameHome))
        return false
    }
}