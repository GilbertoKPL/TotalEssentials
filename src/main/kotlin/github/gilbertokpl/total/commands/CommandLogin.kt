package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.local.LoginData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
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
            maximumSize = 1,
            usage = listOf(
                "P_/login <senha>",
                "totalessentials.commands.login.other_/login <player>",
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        val encrypt = TotalEssentials.basePlugin.getEncrypt()

        if (s is Player && LoginData.checkIfPlayerExist(s) && !LoginData.checkIfPlayerIsLoggedIn(s)) {

            val password = encrypt.decrypt(LoginData.password[s]!!)

            if (password == args[0]) {
                s.sendMessage(LangConfig.authLoggedIn)
                LoginData.loggedIn[s] = true

                val address = s.address?.address.toString()

                if (LoginData.ip[s] != address) {
                    LoginData.ip[s] = address
                }

                return false
            }

            val attempts = LoginData.attempts[s]!! + 1

            if (attempts == MainConfig.authMaxAttempts) {
                s.kickPlayer(LangConfig.authKickMessage.replace("%quant%", attempts.toString()))
            }

            s.sendMessage(LangConfig.authIncorrectPassword)

            LoginData.attempts[s] = attempts

            return false


        }

        @Suppress("USELESS_IS_CHECK")
        if (s is Player && LoginData.checkIfPlayerIsLoggedIn(s) && s.hasPermission("totalessentials.commands.login.other") || s is CommandSender) {

            if (!LoginData.checkIfPlayerExist(args[0])) {
                s.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }
            if (LoginData.checkIfPlayerIsLoggedIn(args[0])) {
                s.sendMessage(LangConfig.authOtherAlreadyLogged.replace("%player%", args[0]))
                return false
            }

            val p = Bukkit.getPlayer(args[0])

            if (p == null) {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            LoginData.loggedIn[p] = true

            s.sendMessage(LangConfig.authOtherLogin.replace("%player%", p.name))
            p.sendMessage(LangConfig.authLoggedIn)

        }


        return false
    }
}