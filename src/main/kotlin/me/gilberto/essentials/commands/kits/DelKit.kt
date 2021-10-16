package me.gilberto.essentials.commands.kits

import me.gilberto.essentials.commands.kits.Kit.Companion.updatekits
import me.gilberto.essentials.config.configs.langs.General
import me.gilberto.essentials.config.configs.langs.Kits
import me.gilberto.essentials.config.configs.langs.Kits.delkitusage
import me.gilberto.essentials.config.configs.langs.Kits.notexist
import me.gilberto.essentials.database.PlayerKits
import me.gilberto.essentials.database.SqlInstance
import me.gilberto.essentials.database.SqlKits
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class DelKit : CommandExecutor {
    override fun onCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (s !is Player) {
            s.sendMessage(General.onlyplayercommand)
            return false
        }
        if (s.hasPermission("gd.essentials.kits.admin")) {
            if (args.size == 1) {
                try {
                    delkit(args[0].lowercase(), s)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    s.sendMessage(Kits.delkitproblem.replace("%name%", args[0]))
                }
            } else {
                s.sendMessage(delkitusage)
            }
            return false
        }
        s.sendMessage(General.notperm)
        return false
    }

    private fun delkit(kit: String, p: Player) {
        CompletableFuture.runAsync({
            transaction(SqlInstance.SQL) {
                if (SqlKits.select(SqlKits.kitname like kit).empty()) {
                    p.sendMessage(notexist)
                    return@transaction
                }
                SqlKits.deleteWhere { SqlKits.kitname like kit }
                val col = Column<Int>(PlayerKits, kit, IntegerColumnType())
                col.dropStatement()
                p.sendMessage(Kits.delkitsuccess.replace("%name%", kit))
            }
        }, Executors.newCachedThreadPool())
        updatekits()
    }
}