package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.local.LoginData
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.Discord
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandRegister : github.gilbertokpl.core.external.command.CommandCreator("register") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("registrar"),
            active = MainConfig.authActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.register",
            minimumSize = 2,
            maximumSize = 2,
            usage = listOf(
                "P_/registrar <senha> <senha>",
                "totalessentials.commands.register.other_/registrar <player> <senha>",
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        val encrypt = TotalEssentials.basePlugin.getEncrypt()

        if (s is Player && !LoginData.doesPlayerExist(s)) {

            val vpn = PlayerData.playerInfo[s]?.get(3) ?: false

            if (vpn == true) {
                s.sendMessage(LangConfig.authVpn)
                return false
            }

            if (args[0] != args[1]) {
                s.sendMessage(LangConfig.authDifferentPasswords)
                return false
            }

            if (args[0].length >= 16) {
                s.sendMessage(LangConfig.authPasswordMaxLength)
                return false
            }

            if (args[0].length < 5) {
                s.sendMessage(LangConfig.authPasswordMinLength)
                return false
            }

            var quant = 0

            val playerAddress = s.address?.address.toString()

            for (i in LoginData.ipAddress.getMap().values) {
                if (i == playerAddress) {
                    quant += 1
                }
            }

            if (quant >= MainConfig.authMaxRegister) {
                s.sendMessage(LangConfig.authMaxRegister.replace("%max%", quant.toString()))
                return false
            }

            val info = PlayerData.playerInfo[s]

            LoginData.createNewLoginData(s.name.lowercase(), encrypt.encrypt(args[0]), playerAddress)

            s.sendMessage(LangConfig.authRegisterSuccess)

            val message = LangConfig.discordchatSendPlayerLocale
                .replace("%player%", s.name)
                .replace("%ip%", playerAddress)
                .replace("%country%", info?.get(0) ?: "none")
                .replace("%state%",info?.get(1) ?: "none")
                .replace("%city%",info?.get(2) ?: "none")


            if (MainConfig.discordbotConnectRegisterChat) {
                Discord.sendDiscordMessage(message, MainConfig.discordbotIdRegisterChat, false)
            }

            return false

        }

        @Suppress("USELESS_IS_CHECK")
        if (s is Player && LoginData.isPlayerLoggedIn(s) && s.hasPermission("totalessentials.commands.register.other") || s is CommandSender) {
            if (LoginData.doesPlayerExist(args[0])) {
                s.sendMessage(LangConfig.generalPlayerExist)
                return false
            }

            LoginData.createNewLoginData(args[0].lowercase(), encrypt.encrypt(args[1]), "127.0.0.1")

            s.sendMessage(LangConfig.authOtherRegister.replace("%player%", args[0].lowercase()))

            val p = Bukkit.getPlayer(args[0]) ?: return false

            LoginData.isLoggedIn[p] = true

            p.sendMessage(LangConfig.authLoggedIn)

        }


        return false
    }
}