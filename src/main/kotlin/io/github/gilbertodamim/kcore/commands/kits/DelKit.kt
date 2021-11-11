package io.github.gilbertodamim.kcore.commands.kits

import com.github.benmanes.caffeine.cache.Caffeine
import io.github.gilbertodamim.kcore.config.configs.KitsConfig
import io.github.gilbertodamim.kcore.config.configs.KitsConfig.enableSounds
import io.github.gilbertodamim.kcore.config.langs.GeneralLang
import io.github.gilbertodamim.kcore.config.langs.KitsLang
import io.github.gilbertodamim.kcore.config.langs.KitsLang.delKitUsage
import io.github.gilbertodamim.kcore.config.langs.KitsLang.notExist
import io.github.gilbertodamim.kcore.dao.Dao.kitPlayerCache
import io.github.gilbertodamim.kcore.dao.Dao.kitsCache
import io.github.gilbertodamim.kcore.database.SqlInstance
import io.github.gilbertodamim.kcore.database.table.PlayerKits
import io.github.gilbertodamim.kcore.database.table.PlayerKits.uuid
import io.github.gilbertodamim.kcore.database.table.SqlKits
import io.github.gilbertodamim.kcore.inventory.KitsInventory
import io.github.gilbertodamim.kcore.management.ErrorClass
import io.github.gilbertodamim.kcore.management.Manager.sendMessageWithSound
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
                    kitsCache.getIfPresent(args[0].lowercase()).also {
                        it ?: s.sendMessageWithSound(notExist, KitsConfig.problem, enableSounds).run {
                            return true
                        }
                        delKit(args[0].lowercase(), s)
                    }
                } catch (ex: Exception) {
                    ErrorClass.sendException(ex)
                    s.sendMessageWithSound(KitsLang.delKitProblem.replace("%name%", args[0]), KitsConfig.problem, enableSounds)
                }
                return true
            }
            s.sendMessageWithSound(delKitUsage, KitsConfig.problem, enableSounds)
            return true
        }
        s.sendMessageWithSound(GeneralLang.notPerm, KitsConfig.problem, enableSounds)
        return true
    }

    private fun delKit(kit: String, p: Player) {
        kitsCache.invalidate(kit)
        KitsInventory.kitGuiInventory()
        CompletableFuture.runAsync({
            try {
                transaction(SqlInstance.SQL) {
                    SqlKits.deleteWhere { SqlKits.kitName eq kit }
                    for (i in PlayerKits.selectAll()) {
                        var list = ""
                        val present = kitPlayerCache.getIfPresent(i[uuid]) != null
                        val cache = if (present) {
                            Caffeine.newBuilder().maximumSize(500).build<String, Long>()
                        } else {
                            null
                        }
                        for (values in i[PlayerKits.kitsTime].split("-")) {
                            values.split(".").also {
                                if (it[0] != kit) {
                                    if (list == "") {
                                        list = values
                                    } else {
                                        list += "$list-$values"
                                    }
                                    if (present) {
                                        cache?.put(it[0], it[1].toLong())
                                    }
                                }
                            }
                        }
                        PlayerKits.update({ uuid like i[uuid] }) {
                            it[kitsTime] = list
                        }
                        if (present) {
                            kitPlayerCache.invalidate(i[uuid])
                            kitPlayerCache.put(i[uuid], cache!!)
                        }
                    }
                }
                p.sendMessageWithSound(KitsLang.delKitSuccess.replace("%name%", kit), KitsConfig.success, enableSounds)
            } catch (ex: Exception) {
                ErrorClass.sendException(ex)
            }
        }, Executors.newCachedThreadPool())
    }
}