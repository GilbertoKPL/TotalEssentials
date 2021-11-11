package io.github.gilbertodamim.kcore.commands

import io.github.gilbertodamim.kcore.database.SqlInstance
import io.github.gilbertodamim.kcore.database.table.PlayerKits
import io.github.gilbertodamim.kcore.database.table.SqlKits
import io.github.gilbertodamim.kcore.management.ErrorClass
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class KCore : CommandExecutor {
    override fun onCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.size > 1) {
            if (args[0].equals("insert", true)) {
                CompletableFuture.runAsync({
                    try {
                        transaction(SqlInstance.SQL) {
                            val array = ArrayList<Column<Long>>()
                            for (column in SqlKits.selectAll()) {
                                val colTo = Column<Long>(PlayerKits, column[SqlKits.kitName], LongColumnType())
                                array.add(colTo)
                            }
                            for (i in 1..args[2].toInt()) {
                                PlayerKits.insert {
                                    it[uuid] = "${args[1]}$i"
                                    for (column in array) {
                                        it[column] = 0L
                                    }
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        ErrorClass.sendException(ex)
                    }
                }, Executors.newSingleThreadExecutor())
            }
        }
        return false
    }
}