package me.gilberto.essentials.commands

import me.gilberto.essentials.EssentialsMain.disableplugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

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