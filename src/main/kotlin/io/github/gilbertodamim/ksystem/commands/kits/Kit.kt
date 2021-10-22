package io.github.gilbertodamim.ksystem.commands.kits

import io.github.gilbertodamim.ksystem.config.langs.GeneralLang.onlyPlayerCommand
import io.github.gilbertodamim.ksystem.database.SqlInstance
import io.github.gilbertodamim.ksystem.database.table.PlayerKits
import io.github.gilbertodamim.ksystem.database.table.SqlKits
import io.github.gilbertodamim.ksystem.management.dao.Dao
import io.github.gilbertodamim.ksystem.management.dao.Dao.kitsCache
import io.github.gilbertodamim.ksystem.management.dao.KSystemKit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors


@Suppress("DEPRECATION")
class Kit : CommandExecutor {
    override fun onCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (s !is Player) {
            s.sendMessage(onlyPlayerCommand)
            return false
        }
        if (args.isNotEmpty() && s.hasPermission("ksystem.kits")) {
            val to = kitsCache[args[0].lowercase()] ?: return false
            for (i in to.items) {
                s.player?.inventory?.addItem(i ?: continue)
            }
        }
        return false
    }

    companion object {
        fun startKits() {
            try {
                transaction(SqlInstance.SQL) {
                    SchemaUtils.create(SqlKits, PlayerKits)
                }
            } finally {
                updateKits()
            }
        }

        fun updateKits() {
            Dao.inUpdate = true
            CompletableFuture.runAsync({
                try {
                    transaction(SqlInstance.SQL) {
                        for (values in SqlKits.selectAll()) {
                            val kit = values[SqlKits.kitName]
                            val kitRealName = values[SqlKits.kitRealName]
                            val kitTime = values[SqlKits.kitTime]
                            val item = values[SqlKits.kitItems]
                            kitsCache[kit] = KSystemKit(kit, kitTime, kitRealName, convertItems(item))
                        }
                        Dao.inUpdate = false
                    }
                }
                catch (e : Exception) {
                    e.printStackTrace()
                }
            }, Executors.newCachedThreadPool())
        }

        @Throws(IllegalStateException::class)
        fun convertItems(items: Array<ItemStack?>): String {
            return try {
                val outputStream = ByteArrayOutputStream()
                val dataOutput = BukkitObjectOutputStream(outputStream)
                dataOutput.writeInt(items.size)
                for (i in items.indices) {
                    dataOutput.writeObject(items[i])
                }
                dataOutput.close()
                Base64Coder.encodeLines(outputStream.toByteArray())
            } catch (e: Exception) {
                throw IllegalStateException(e)
            }
        }
        @Throws(IOException::class)
        fun convertItems(data: String): Array<ItemStack?> {
            return try {
                val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
                val dataInput = BukkitObjectInputStream(inputStream)
                val items = arrayOfNulls<ItemStack>(dataInput.readInt())
                for (i in items.indices) {
                    items[i] = dataInput.readObject() as ItemStack?
                }
                dataInput.close()
                items
            } catch (e: ClassNotFoundException) {
                throw IOException(e)
            }
        }
    }
}