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

class CommandLogin : CommandManager("login") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("logar"),
            active = MainConfig.authActivated,
            target = CommandTargetType.ALL,
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

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        val encrypt = TotalEssentials.getCore().getEncrypt()

        if (sender is Player && LoginData.doesPlayerExist(sender) && !LoginData.isPlayerLoggedIn(sender)) {

            val password = encrypt.decrypt(LoginData.password[sender]!!)

            if (password == args[0]) {
                sender.sendMessage(LangConfig.authLoggedIn)
                LoginData.isLoggedIn[sender] = true

                val address = sender.address?.address.toString()

                if (LoginData.ipAddress[sender] != address) {
                    LoginData.ipAddress[sender] = address

                    val info = PlayerData.playerInfo[sender]

                    val message = LangConfig.discordchatSendPlayerLocalAtt
                        .replace("%player%", sender.name)
                        .replace("%ip%", address)
                        .replace("%country%", info?.get(0) ?: "none")
                        .replace("%state%", info?.get(1) ?: "none")
                        .replace("%city%", info?.get(2) ?: "none")


                    if (MainConfig.discordbotConnectRegisterChat) {
                        DiscordManager.sendDiscordMessage(message, MainConfig.discordbotIdRegisterChat, false)
                    }
                }

                return false
            }

            val attempts = LoginData.loginAttempts[sender]!! + 1

            if (attempts == MainConfig.authMaxAttempts) {
                sender.kickPlayer(LangConfig.authKickMessage.replace("%quant%", attempts.toString()))
            }

            sender.sendMessage(LangConfig.authIncorrectPassword)

            LoginData.loginAttempts[sender] = attempts

            return false


        }

        if (args[0] == "ip" && args.size == 2 && sender.hasPermission("totalessentials.commands.login.ip") || args[0] == "ip" && args.size == 2 && sender !is Player) {
            val ip = LoginData.ipAddress[args[1]] ?: "0.0.0.0"
            sender.sendMessage(LangConfig.authIpMessage.replace("%ip%", ip))

            return false
        }

        if (sender is Player && LoginData.isPlayerLoggedIn(sender) && sender.hasPermission("totalessentials.commands.login.other") || sender !is Player) {

            if (!LoginData.doesPlayerExist(args[0])) {
                sender.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }
            if (LoginData.isPlayerLoggedIn(args[0])) {
                sender.sendMessage(LangConfig.authOtherAlreadyLogged.replace("%player%", args[0]))
                return false
            }

            val p = Bukkit.getPlayer(args[0])

            if (p == null) {
                sender.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            LoginData.isLoggedIn[p] = true

            sender.sendMessage(LangConfig.authOtherLogin.replace("%player%", p.name))
            p.sendMessage(LangConfig.authLoggedIn)

        }


        return false
    }
}