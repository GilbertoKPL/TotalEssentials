package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.LoginData
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.DiscordManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandRegister : CommandManager("register") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("registrar"),
            active = MainConfig.authActivated,
            target = CommandTargetType.ALL,
            countdown = 0,
            permission = "totalessentials.commands.register",
            minimumSize = 2,
            maximumSize = 2,
            usage = listOf(
                "P_/registrar <password> <password>",
                "totalessentials.commands.register.other_/registrar <player> <password>"
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        val encrypt = TotalEssentials.getCore().getEncrypt()

        // self register
        if (sender is Player && !LoginData.doesPlayerExist(sender)) {

            val vpn = PlayerData.playerInfo[sender]?.getOrNull(3) ?: "false"

            if (vpn == "true") {
                sender.sendMessage(LangConfig.authVpn)
                return false
            }

            // check passwords match
            if (args[0] != args[1]) {
                sender.sendMessage(LangConfig.authDifferentPasswords)
                return false
            }

            // check password length
            if (args[0].length >= 16) {
                sender.sendMessage(LangConfig.authPasswordMaxLength)
                return false
            }
            if (args[0].length < 5) {
                sender.sendMessage(LangConfig.authPasswordMinLength)
                return false
            }

            // check max registrations per IP
            var quant = 0
            val playerAddress = sender.address?.address.toString()
            for (i in LoginData.ipAddress.getMap().values) {
                if (i == playerAddress) quant += 1
            }

            if (quant >= MainConfig.authMaxRegister) {
                sender.sendMessage(LangConfig.authMaxRegister.replace("%max%", quant.toString()))
                return false
            }

            val info = PlayerData.playerInfo[sender]

            LoginData.createNewLoginData(sender.name.lowercase(), encrypt.encrypt(args[0]), playerAddress)

            sender.sendMessage(LangConfig.authRegisterSuccess)

            // send Discord message
            val message = LangConfig.discordchatSendPlayerLocale
                .replace("%player%", sender.name)
                .replace("%ip%", playerAddress)
                .replace("%country%", info?.getOrNull(0) ?: "none")
                .replace("%state%", info?.getOrNull(1) ?: "none")
                .replace("%city%", info?.getOrNull(2) ?: "none")

            if (MainConfig.discordbotConnectRegisterChat) {
                DiscordManager.sendDiscordMessage(message, MainConfig.discordbotIdRegisterChat, false)
            }

            return false
        }

        // admin register for others
        if ((sender is Player && LoginData.isPlayerLoggedIn(sender) && sender.hasPermission("totalessentials.commands.register.other")) || sender !is Player) {
            if (LoginData.doesPlayerExist(args[0])) {
                sender.sendMessage(LangConfig.generalPlayerExist)
                return false
            }

            LoginData.createNewLoginData(args[0].lowercase(), encrypt.encrypt(args[1]), "127.0.0.1")

            sender.sendMessage(LangConfig.authOtherRegister.replace("%player%", args[0].lowercase()))

            val p = Bukkit.getPlayer(args[0]) ?: return false
            LoginData.isLoggedIn[p] = true
            p.sendMessage(LangConfig.authLoggedIn)
        }

        return false
    }
}
