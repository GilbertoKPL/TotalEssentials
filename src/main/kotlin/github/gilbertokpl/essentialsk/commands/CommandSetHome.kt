package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.PluginUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetHome : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "sethome"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.sethome"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage =
        listOf("/sethome <homeName>", "essentialsk.commands.sethome.other_/sethome <playername>:<homeName>")

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val nameHome = args[0].lowercase()

        //admin
        if (args[0].contains(":") && s.hasPermission("essentialsk.commands.sethome.other")) {
            TaskUtil.getInstance().asyncExecutor {
                val split = args[0].split(":")

                val pName = split[0].lowercase()

                val otherPlayerInstance = PlayerData(pName)

                if (!otherPlayerInstance.checkSql()) {
                    s.sendMessage(GeneralLang.getInstance().generalPlayerNotExist)
                    return@asyncExecutor
                }

                if (split.size < 2) {
                    s.sendMessage(
                        GeneralLang.getInstance().homesHomeOtherList.replace("%player%", pName)
                            .replace("%list%", otherPlayerInstance.getHomeList().toString())
                    )
                    return@asyncExecutor
                }

                if (otherPlayerInstance.getHomeList().contains(split[1])) {
                    s.sendMessage(GeneralLang.getInstance().homesNameAlreadyExist)
                    return@asyncExecutor
                }

                otherPlayerInstance.setHome(split[1].lowercase(), (s as Player).location)

                s.sendMessage(
                    GeneralLang.getInstance().homesHomeOtherCreated.replace("%player%", pName)
                        .replace("%home%", split[1])
                )
            }

            return false
        }

        //check if home name do not contain . or - to not bug
        if (PluginUtil.getInstance().checkSpecialCaracteres(nameHome)) {
            s.sendMessage(GeneralLang.getInstance().generalSpecialCaracteresDisabled)
            return false
        }

        //check length of home name
        if (nameHome.length > 16) {
            s.sendMessage(GeneralLang.getInstance().homesNameLength)
            return false
        }

        val playerInstance = PlayerData(s.name)

        val playerCache = playerInstance.getCache() ?: return false

        //check if already exist
        if (playerCache.homeCache.contains(nameHome)) {
            s.sendMessage(GeneralLang.getInstance().homesNameAlreadyExist)
            return false
        }

        //check limit of homes
        if (playerCache.homeCache.size >= playerCache.homeLimitCache && !s.hasPermission("essentialsk.bypass.homelimit")) {
            s.sendMessage(
                GeneralLang.getInstance().homesHomeLimitCreated.replace(
                    "%limit%",
                    playerCache.homeLimitCache.toString()
                )
            )
            return false
        }

        //check if world is blocked
        if (MainConfig.getInstance().homesBlockWorlds.contains((s as Player).world.name.lowercase())) {
            s.sendMessage(GeneralLang.getInstance().homesHomeWorldBlocked)
            return false
        }

        playerInstance.setHome(nameHome, s.location)
        s.sendMessage(GeneralLang.getInstance().homesHomeCreated.replace("%home%", nameHome))

        return false
    }
}