package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.TotalEssentialsJava
import github.gilbertokpl.total.cache.local.LoginData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.LoginUtil
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandChangePass : github.gilbertokpl.core.external.command.CommandCreator("changepass") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("mudarsenha"),
            active = MainConfig.authActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.changepass",
            minimumSize = 2,
            maximumSize = 2,
            usage = listOf(
                "P_/mudarsenha <antigaSenha> <senha>",
                "totalessentials.commands.changepass.other_/mudarsenha <player> <senha>",
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        val encrypt = TotalEssentialsJava.basePlugin.getEncrypt()

        if (s is Player && LoginData.doesPlayerExist(s) && LoginData.isPlayerLoggedIn(s)) {
            val password = encrypt.decrypt(LoginData.password[s]!!)

            if (password == args[0]) {
                LoginData.password[s] = encrypt.encrypt(args[1])
                s.sendMessage(LangConfig.authChangePass)
                return false
            }

            if (s.hasPermission("totalessentials.commands.changepass.other") && !LoginData.doesPlayerExist(args[0])) {
                s.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }

            if (s.hasPermission("totalessentials.commands.changepass.other") && LoginData.doesPlayerExist(args[0])) {

                LoginData.values[args[0]] = 0

                LoginData.password[args[0]] = encrypt.encrypt(args[1])

                LoginData.ipAddress[args[0]] = "127.0.0.1"

                LoginData.isLoggedIn[args[0]] = false

                s.sendMessage(LangConfig.authOtherChangePass.replace("%player%", args[0]))

                val p = Bukkit.getPlayer(args[0])

                if (p != null) {
                    LoginUtil.loginMessage(p)
                }

                return false
            }

            s.sendMessage(LangConfig.authIncorrectPassword)

            return false
        }

        if (!LoginData.doesPlayerExist(args[0])) {
            s.sendMessage(LangConfig.generalPlayerNotExist)
            return false
        }

        LoginData.values[args[0]] = 0

        LoginData.password[args[0]] = encrypt.encrypt(args[1])

        LoginData.ipAddress[args[0]] = "127.0.0.1"

        LoginData.isLoggedIn[args[0]] = false

        val p = Bukkit.getPlayer(args[0])

        if (p != null) {
            LoginUtil.loginMessage(p)
        }

        s.sendMessage(LangConfig.authOtherChangePass.replace("%player%", args[0]))

        return false
    }
}