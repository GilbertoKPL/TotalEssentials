package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.data.`object`.OfflinePlayerData
import github.genesyspl.cardinal.manager.ICommand
import github.genesyspl.cardinal.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandDelHome : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "delhome"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.delhome"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage =
        listOf("/delhome <homeName>", "cardinal.commands.delhome.other_/delhome <playername>:<homeName>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        //admin
        if (args[0].contains(":") && s.hasPermission("cardinal.commands.delhome.other")) {
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

                if (!otherPlayerInstance.getHomeList().contains(split[1])) {
                    s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesNameDontExist)
                    return@asyncExecutor
                }

                otherPlayerInstance.delHome(split[1])

                s.sendMessage(
                    github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesHomeOtherRemoved.replace(
                        "%player%",
                        pName
                    )
                        .replace("%home%", split[1])
                )
            }

            return false
        }

        val nameHome = args[0].lowercase()

        val playerInstance = DataManager.getInstance().playerCacheV2[s.name.lowercase()] ?: return false

        //check if home don't exist
        if (!playerInstance.homeCache.contains(nameHome)) {
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesNameDontExist)
            return false
        }

        playerInstance.delHome(nameHome)

        s.sendMessage(
            github.genesyspl.cardinal.configs.GeneralLang.getInstance().homesHomeRemoved.replace(
                "%home%",
                nameHome
            )
        )
        return false
    }
}