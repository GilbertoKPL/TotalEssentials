package io.github.gilbertodamim.ksystem.commands.kits

import io.github.gilbertodamim.ksystem.commands.kits.Kit.Companion.updateKits
import io.github.gilbertodamim.ksystem.config.langs.GeneralLang
import io.github.gilbertodamim.ksystem.config.langs.KitsLang
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.delKitUsage
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.notExist
import io.github.gilbertodamim.ksystem.database.SqlInstance
import io.github.gilbertodamim.ksystem.database.table.PlayerKits
import io.github.gilbertodamim.ksystem.database.table.SqlKits
import io.github.gilbertodamim.ksystem.management.dao.Dao.kitsCache
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class DelKit : CommandExecutor {
    override fun onCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (s !is Player) {
            s.sendMessage(GeneralLang.onlyPlayerCommand)
            return false
        }
        if (s.hasPermission("ksystem.kits.admin")) {
            if (args.size == 1) {
                try {
                    if (kitsCache.getIfPresent(args[0].lowercase()) == null) {
                        s.sendMessage(notExist)
                    } else {
                        delKit(args[0].lowercase(), s)
                    }

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
                    val col = Column<Long>(PlayerKits, kit, LongColumnType())
                    col.dropStatement().forEach { statement ->
                        exec(statement)
                    }
                }
                p.sendMessage(KitsLang.delKitSuccess.replace("%name%", kit))
                updateKits()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Executors.newCachedThreadPool())
    }
}