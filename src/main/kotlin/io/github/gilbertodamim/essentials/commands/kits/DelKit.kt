package io.github.gilbertodamim.essentials.commands.kits

import io.github.gilbertodamim.essentials.commands.kits.Kit.Companion.updateKits
import io.github.gilbertodamim.essentials.config.langs.GeneralLang
import io.github.gilbertodamim.essentials.config.langs.KitsLang
import io.github.gilbertodamim.essentials.config.langs.KitsLang.delKitUsage
import io.github.gilbertodamim.essentials.config.langs.KitsLang.notExist
import io.github.gilbertodamim.essentials.database.SqlInstance
import io.github.gilbertodamim.essentials.database.table.PlayerKits
import io.github.gilbertodamim.essentials.database.table.SqlKits
import io.github.gilbertodamim.essentials.management.dao.Dao.kitsCache
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class DelKit : CommandExecutor {
    override fun onCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (s !is Player) {
            s.sendMessage(GeneralLang.onlyPlayerCommand)
            return false
        }
        if (s.hasPermission("gd.essentials.kits.admin")) {
            if (args.size == 1) {
                try {
                    kitsCache[args[0].lowercase()] ?: run {
                        s.sendMessage(notExist)
                        return false
                    }
                    delKit(args[0].lowercase(), s)

                } catch (ex: Exception) {
                    ex.printStackTrace()
                    s.sendMessage(KitsLang.delKitProblem.replace("%name%", args[0]))
                }
            } else {
                s.sendMessage(delKitUsage)
            }
            return false
        }
        s.sendMessage(GeneralLang.notPerm)
        return false
    }

    private fun delKit(kit: String, p: Player) {
        CompletableFuture.runAsync({
            try {
                transaction(SqlInstance.SQL) {
                    SqlKits.deleteWhere { SqlKits.kitName like kit }
                    val col = Column<Int>(PlayerKits, kit, IntegerColumnType())
                    col.dropStatement().forEach { statement ->
                        exec(statement)
                    }
                }
                p.sendMessage(KitsLang.delKitSuccess.replace("%name%", kit))
                updateKits()
            }
            catch (e : Exception) {
                e.printStackTrace()
            }
        }, Executors.newCachedThreadPool())
    }
}