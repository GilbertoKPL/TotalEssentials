package io.github.gilbertodamim.kcore.commands.kits

import io.github.gilbertodamim.kcore.config.langs.GeneralLang
import io.github.gilbertodamim.kcore.config.langs.KitsLang
import io.github.gilbertodamim.kcore.config.langs.KitsLang.delKitUsage
import io.github.gilbertodamim.kcore.config.langs.KitsLang.notExist
import io.github.gilbertodamim.kcore.database.SqlInstance
import io.github.gilbertodamim.kcore.database.table.PlayerKits
import io.github.gilbertodamim.kcore.database.table.PlayerKits.uuid
import io.github.gilbertodamim.kcore.database.table.SqlKits
import io.github.gilbertodamim.kcore.inventory.KitsInventory
import io.github.gilbertodamim.kcore.management.ErrorClass
import io.github.gilbertodamim.kcore.dao.Dao.kitsCache
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
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
        kitsCache.invalidate(kit)
        KitsInventory().kitGuiInventory()
        CompletableFuture.runAsync({
            try {
                transaction(SqlInstance.SQL) {
                    SqlKits.deleteWhere { SqlKits.kitName eq kit }
                    for (i in PlayerKits.selectAll()) {
                        var list = ""
                        for (values in i[PlayerKits.kitsTime].split("-")) {
                            if (values.split(".")[0] != kit) {
                                if (list == "") {
                                    list = values
                                } else {
                                    list += "$list-$values"
                                }
                            }
                        }
                        PlayerKits.update({ uuid like i[uuid] }) {
                            it[kitsTime] = list
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