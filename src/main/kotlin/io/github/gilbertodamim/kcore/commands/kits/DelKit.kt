package io.github.gilbertodamim.kcore.commands.kits

import io.github.gilbertodamim.kcore.config.langs.GeneralLang
import io.github.gilbertodamim.kcore.config.langs.KitsLang
import io.github.gilbertodamim.kcore.config.langs.KitsLang.delKitUsage
import io.github.gilbertodamim.kcore.config.langs.KitsLang.notExist
import io.github.gilbertodamim.kcore.database.SqlInstance
import io.github.gilbertodamim.kcore.database.table.PlayerKits
import io.github.gilbertodamim.kcore.database.table.SqlKits
import io.github.gilbertodamim.kcore.inventory.KitsInventory
import io.github.gilbertodamim.kcore.management.ErrorClass
import io.github.gilbertodamim.kcore.management.dao.Dao.kitsCache
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class DelKit : CommandExecutor {
    override fun onCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (s !is Player) {
            s.sendMessage(GeneralLang.onlyPlayerCommand)
            return false
        }
        if (s.hasPermission("kcore.kits.admin")) {
            if (args.size == 1) {
                try {
                    if (kitsCache.getIfPresent(args[0].lowercase()) == null) {
                        s.sendMessage(notExist)
                    } else {
                        delKit(args[0].lowercase(), s)
                    }

                } catch (ex: Exception) {
                    ErrorClass().sendException(ex)
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
        kitsCache.synchronous().invalidate(kit)
        KitsInventory().kitGuiInventory()
        CompletableFuture.runAsync({
            try {
                transaction(SqlInstance.SQL) {
                    if (PlayerKits.columns.size == 2) {
                        for (i in PlayerKits.selectAll()) {
                            PlayerKits.deleteWhere { PlayerKits.uuid like i[PlayerKits.uuid] }
                        }
                    }
                    SqlKits.deleteWhere { SqlKits.kitName like kit }

                    val hashmap = HashMap<String, HashMap<Column<Long>, Long>>()
                    val col = Column<Long>(PlayerKits, kit, LongColumnType())
                    for (i in PlayerKits.selectAll()) {
                        val name = i[PlayerKits.uuid]
                        val internalHashmap = HashMap<Column<Long>, Long>()
                        for (column in PlayerKits.columns) {
                            if (column == PlayerKits.uuid) continue
                            if (column == col) continue
                            val colTo = Column<Long>(PlayerKits, column.name, LongColumnType())
                            internalHashmap[colTo] = i[colTo]
                        }
                        hashmap[name] = internalHashmap
                        PlayerKits.deleteWhere { PlayerKits.uuid eq name }
                    }
                    col.dropStatement().forEach { statement ->
                        exec(statement)
                    }
                    for (i in hashmap) {
                        PlayerKits.insert {
                            it[uuid] = i.key
                            for (column in i.value) {
                                it[column.key] = column.value
                            }
                        }
                    }
                }
                p.sendMessage(KitsLang.delKitSuccess.replace("%name%", kit))
            } catch (ex: Exception) {
                ErrorClass().sendException(ex)
            }
        }, Executors.newCachedThreadPool())
    }
}