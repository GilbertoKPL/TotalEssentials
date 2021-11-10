package io.github.gilbertodamim.kcore.commands.kits

import io.github.gilbertodamim.kcore.config.configs.KitsConfig
import io.github.gilbertodamim.kcore.config.langs.GeneralLang
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class GiveKit : CommandExecutor {
    override fun onCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (s.hasPermission("kcore.kits.admin")) {

        }
        s.sendMessage(GeneralLang.notPerm, KitsConfig.problem)
        return false
    }
}