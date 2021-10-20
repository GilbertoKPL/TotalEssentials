package io.github.gilbertodamim.essentials.commands

import io.github.gilbertodamim.essentials.EssentialsMain.disableplugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class Essentials : CommandExecutor {
    override fun onCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args[0].isNotEmpty()) {
            if (args[0] == "reload") {
                disableplugin(false)
            }
        }
        return false
    }
}