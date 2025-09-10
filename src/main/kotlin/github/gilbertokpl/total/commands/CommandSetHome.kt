package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.ServerUtil
import github.gilbertokpl.total.util.PermissionUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetHome : CommandManager("sethome") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("setarhome"),
            active = MainConfig.homesActivated,
            target = CommandTargetType.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.sethome",
            minimumSize = 1,
            maximumSize = 1,
            usage = listOf(
                "/sethome <homeName>",
                "totalessentials.commands.sethome.other_/sethome <playername>:<homeName>"
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        val nameHome = args[0].lowercase()

        // admin set home for others
        if (args[0].contains(":") && sender.hasPermission("totalessentials.commands.sethome.other")) {
            val split = args[0].split(":")
            val pName = split[0]

            if (!PlayerData.checkIfPlayerExists(pName)) {
                sender.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }

            val homes = PlayerData.homeCache[pName]!!

            if (split.size < 2) {
                sender.sendMessage(
                    LangConfig.homesOtherList.replace("%player%", pName)
                        .replace("%list%", homes.map { it.key }.toString())
                )
                return false
            }

            if (homes.contains(split[1])) {
                sender.sendMessage(LangConfig.homesNameAlreadyExist)
                return false
            }

            homes[split[1]] = (sender as Player).location

            sender.sendMessage(
                LangConfig.homesOtherCreated.replace("%player%", pName)
                    .replace("%home%", split[1])
            )
            return false
        }

        sender as Player

        // check invalid characters
        if (ServerUtil.checkSpecialCharacters(nameHome)) {
            sender.sendMessage(LangConfig.generalSpecialCaracteresDisabled)
            return false
        }

        // check name length
        if (nameHome.length > 16) {
            sender.sendMessage(LangConfig.homesNameLength)
            return false
        }

        // update home limit if needed
        if (!sender.hasPermission("totalessentials.commands.sethome." + PlayerData.homeLimitCache[sender])) {
            PlayerData.homeLimitCache[sender] = PermissionUtil.getNumberPermission(
                sender,
                "totalessentials.commands.sethome.",
                MainConfig.homesDefaultLimitHomes
            )
        }

        // check if home limit reached
        if (PlayerData.homeCache[sender]!!.size >= PlayerData.homeLimitCache[sender]!! &&
            !sender.hasPermission("totalessentials.bypass.homelimit")
        ) {
            sender.sendMessage(
                LangConfig.homesLimitMessage.replace(
                    "%limit%",
                    PlayerData.homeLimitCache[sender].toString()
                )
            )
            return false
        }

        // check blocked worlds
        if (MainConfig.homesBlockWorlds.contains(sender.world.name.lowercase()) &&
            !sender.hasPermission("totalessentials.bypass.homeblockedworlds")
        ) {
            sender.sendMessage(LangConfig.homesBlockedWorld)
            return false
        }

        // set home
        PlayerData.homeCache[sender] = hashMapOf(nameHome to sender.location)
        sender.sendMessage(LangConfig.homesCreated.replace("%home%", nameHome))

        return false
    }
}
