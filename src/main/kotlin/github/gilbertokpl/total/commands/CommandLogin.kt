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

class CommandLogin : github.gilbertokpl.core.external.command.CommandCreator("login") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("logar"),
            active = MainConfig.authActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.login",
            minimumSize = 1,
            maximumSize = 2,
            usage = listOf(
                "P_/login <senha>",
                "totalessentials.commands.login.other_/login <player>",
                "totalessentials.commands.login.ip_/login ip <player>",
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        val encrypt = TotalEssentials.basePlugin.getEncrypt()

        if (s is Player && LoginData.doesPlayerExist(s) && !LoginData.isPlayerLoggedIn(s)) {

            val password = encrypt.decrypt(LoginData.password[s]!!)

            if (password == args[0]) {
                s.sendMessage(LangConfig.authLoggedIn)
                LoginData.isLoggedIn[s] = true

                val address = s.address?.address.toString()

                if (LoginData.ipAddress[s] != address) {
                    LoginData.ipAddress[s] = address

                    val info = PlayerData.playerInfo[s]

                    val message = LangConfig.discordchatSendPlayerLocalAtt
                        .replace("%player%", s.name)
                        .replace("%ip%", address)
                        .replace("%country%", info?.get(0) ?: "none")
                        .replace("%state%",info?.get(1) ?: "none")
                        .replace("%city%",info?.get(2) ?: "none")


                    if (MainConfig.discordbotConnectRegisterChat) {
                        Discord.sendDiscordMessage(message, MainConfig.discordbotIdRegisterChat, false)
                    }
                }

                return false
            }

            val attempts = LoginData.loginAttempts[s]!! + 1

            if (attempts == MainConfig.authMaxAttempts) {
                s.kickPlayer(LangConfig.authKickMessage.replace("%quant%", attempts.toString()))
            }

            s.sendMessage(LangConfig.authIncorrectPassword)

            LoginData.loginAttempts[s] = attempts

            return false


        }

        if (args[0] == "ip" && args.size == 2 && s.hasPermission("totalessentials.commands.login.ip") || args[0] == "ip" && args.size == 2 && s !is Player) {
            val ip = LoginData.ipAddress[args[1]] ?: "0.0.0.0"
            s.sendMessage(LangConfig.authIpMessage.replace("%ip%", ip))

            return false
        }

        if (s is Player && LoginData.isPlayerLoggedIn(s) && s.hasPermission("totalessentials.commands.login.other") || s !is Player) {

            if (!LoginData.doesPlayerExist(args[0])) {
                s.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }
            if (LoginData.isPlayerLoggedIn(args[0])) {
                s.sendMessage(LangConfig.authOtherAlreadyLogged.replace("%player%", args[0]))
                return false
            }

            val p = Bukkit.getPlayer(args[0])

            if (p == null) {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            LoginData.isLoggedIn[p] = true

            s.sendMessage(LangConfig.authOtherLogin.replace("%player%", p.name))
            p.sendMessage(LangConfig.authLoggedIn)

        }


        return false
    }
}