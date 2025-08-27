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
                "totalessentials.commands.changepass.other_/mudarsenha <player> <senha>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        val encrypt = TotalEssentialsJava.getBasePlugin().getEncrypt()

        // --------------------------------------------------------
        // Case 1: Sender is a player and changing own password
        // --------------------------------------------------------
        if (s is Player && LoginData.doesPlayerExist(s) && LoginData.isPlayerLoggedIn(s)) {
            val currentPassword = encrypt.decrypt(LoginData.password[s] ?: "")

            // Correct old password
            if (currentPassword == args[0]) {
                LoginData.password[s] = encrypt.encrypt(args[1])
                s.sendMessage(LangConfig.authChangePass)
                return false
            }

            // Attempt to change another player's password
            if (s.hasPermission("totalessentials.commands.changepass.other")) {
                return changeOtherPassword(s, args[0], args[1])
            }

            // Incorrect own password
            s.sendMessage(LangConfig.authIncorrectPassword)
            return false
        }

        // --------------------------------------------------------
        // Case 2: Changing password of another player as console or admin
        // --------------------------------------------------------
        return changeOtherPassword(s, args[0], args[1])
    }

    // --------------------------------------------------------
    // Function to handle changing password of another player
    // --------------------------------------------------------
    private fun changeOtherPassword(sender: CommandSender, targetName: String, newPassword: String): Boolean {
        val encrypt = TotalEssentialsJava.getBasePlugin().getEncrypt()

        if (!LoginData.doesPlayerExist(targetName)) {
            sender.sendMessage(LangConfig.generalPlayerNotExist)
            return false
        }

        // Reset player login values
        LoginData.values[targetName] = 0
        LoginData.password[targetName] = encrypt.encrypt(newPassword)
        LoginData.ipAddress[targetName] = "127.0.0.1"
        LoginData.isLoggedIn[targetName] = false

        // Notify player if online
        Bukkit.getPlayer(targetName)?.let { LoginUtil.loginMessage(it) }

        // Notify sender
        sender.sendMessage(LangConfig.authOtherChangePass.replace("%player%", targetName))
        return false
    }
}
