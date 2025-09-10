package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.LoginData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.login.LoginManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandChangePass : CommandManager("changepass") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("mudarsenha"),
            active = MainConfig.authActivated,
            target = CommandTargetType.ALL,
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

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        val encrypt = TotalEssentials.getCore().getEncrypt()

        // --------------------------------------------------------
        // Case 1: Sender is a player and changing own password
        // --------------------------------------------------------
        if (sender is Player && LoginData.doesPlayerExist(sender) && LoginData.isPlayerLoggedIn(sender)) {
            val currentPassword = encrypt.decrypt(LoginData.password[sender] ?: "")

            // Correct old password
            if (currentPassword == args[0]) {
                LoginData.password[sender] = encrypt.encrypt(args[1])
                sender.sendMessage(LangConfig.authChangePass)
                return false
            }

            // Attempt to change another player's password
            if (sender.hasPermission("totalessentials.commands.changepass.other")) {
                return changeOtherPassword(sender, args[0], args[1])
            }

            // Incorrect own password
            sender.sendMessage(LangConfig.authIncorrectPassword)
            return false
        }

        // --------------------------------------------------------
        // Case 2: Changing password of another player as console or admin
        // --------------------------------------------------------
        return changeOtherPassword(sender, args[0], args[1])
    }

    // --------------------------------------------------------
    // Function to handle changing password of another player
    // --------------------------------------------------------
    private fun changeOtherPassword(sender: CommandSender, targetName: String, newPassword: String): Boolean {
        val encrypt = TotalEssentials.getCore().getEncrypt()

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
        Bukkit.getPlayer(targetName)?.let { LoginManager.loginMessage(it) }

        // Notify sender
        sender.sendMessage(LangConfig.authOtherChangePass.replace("%player%", targetName))
        return false
    }
}
