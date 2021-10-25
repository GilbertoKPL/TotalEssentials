package io.github.gilbertodamim.ksystem.commands.kits

import io.github.gilbertodamim.ksystem.KSystemMain
import io.github.gilbertodamim.ksystem.config.langs.GeneralLang.onlyPlayerCommand
import io.github.gilbertodamim.ksystem.config.langs.KitsLang
import io.github.gilbertodamim.ksystem.database.SqlInstance
import io.github.gilbertodamim.ksystem.database.table.PlayerKits
import io.github.gilbertodamim.ksystem.database.table.SqlKits
import io.github.gilbertodamim.ksystem.inventory.Api
import io.github.gilbertodamim.ksystem.inventory.KitsInventory
import io.github.gilbertodamim.ksystem.management.Manager.getPlayerUUID
import io.github.gilbertodamim.ksystem.management.dao.Dao
import io.github.gilbertodamim.ksystem.management.dao.Dao.KitPlayerCache
import io.github.gilbertodamim.ksystem.management.dao.Dao.kitClickGuiCache
import io.github.gilbertodamim.ksystem.management.dao.Dao.kitGuiCache
import io.github.gilbertodamim.ksystem.management.dao.Dao.kitsCache
import io.github.gilbertodamim.ksystem.management.dao.KSystemKit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors


class Kit : CommandExecutor {
    override fun onCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (s !is Player) {
            s.sendMessage(onlyPlayerCommand)
            return false
        }
        if (args.isNotEmpty() && s.hasPermission("ksystem.kits")) {
            val to = kitsCache.getIfPresent(args[0].lowercase()) ?: return false
            for (i in to.get().items) {
                s.player?.inventory?.addItem(i ?: continue)
            }
        }
        else {
            val inv = kitGuiCache.getIfPresent(1)
            if (inv != null) {
                s.openInventory(inv)
            }
        }
        return false
    }

    private fun reloadKitCache() {
        KitPlayerCache.cleanUp()
        CompletableFuture.runAsync({
            try {
                transaction(SqlInstance.SQL) {
                    for (i in PlayerKits.selectAll()) {
                        val hashMap = HashMap<String, Long>()
                        for (col in PlayerKits.columns) {
                            if (col == PlayerKits.uuid) continue
                            hashMap[col.name] = i[col] as Long
                        }
                        KitPlayerCache.put(i[PlayerKits.uuid], hashMap)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Executors.newSingleThreadExecutor())
    }

    private fun upDatePlayerKitTime(p: Player, kit: String) {
        val pUUID = getPlayerUUID(p)
        CompletableFuture.runAsync({
            try {
                val hashMap = KitPlayerCache.getIfPresent(pUUID)
                if (hashMap != null) {
                    hashMap.remove(kit)
                    hashMap[kit] = System.currentTimeMillis()
                    KitPlayerCache.invalidate(pUUID)
                    KitPlayerCache.put(pUUID, hashMap)
                }
                transaction(SqlInstance.SQL) {
                    val work = PlayerKits.select { PlayerKits.uuid eq pUUID }
                    val col = Column<Long>(PlayerKits, kit, LongColumnType())
                    if (work.empty()) {
                        PlayerKits.insert {
                            it[uuid] = pUUID
                            it[col] = System.currentTimeMillis()
                        }
                        putPlayersCache(p)
                    }
                    else {
                        PlayerKits.update({ PlayerKits.uuid eq pUUID }) {
                            it[col] = System.currentTimeMillis()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Executors.newSingleThreadExecutor())
    }

    private fun putPlayersCache(p: Player) {
        val pUUID = getPlayerUUID(p)
        CompletableFuture.runAsync({
            try {
                transaction(SqlInstance.SQL) {
                    val hashMap = HashMap<String, Long>()
                    val work = PlayerKits.select { PlayerKits.uuid eq pUUID }
                    if (work.empty()) {
                        PlayerKits.insert {
                            it[uuid] = pUUID
                        }
                    }
                    for (i in PlayerKits.columns) {
                        if (i == PlayerKits.uuid) continue
                        hashMap[i.name] = work.single()[i] as Long
                    }
                    KitPlayerCache.put(pUUID, hashMap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Executors.newSingleThreadExecutor())
    }

    private fun pickupKit(p: Player) {
        CompletableFuture.runAsync({
            try {
                transaction(SqlInstance.SQL) {

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Executors.newSingleThreadExecutor())
    }

    private fun kitGui(kit: String, p: Player) {
        val inv = KSystemMain.instance.server.createInventory(null, 36, "Kit $kit")
        for (i in kitsCache.getIfPresent(kit)!!.get().items) {
            if (i == null) continue
            inv.addItem(i)
        }
        inv.setItem(27, Api.item(Material.HOPPER, KitsLang.kitInventoryIconBackName))
        p.openInventory(inv)
    }

    fun kitGuiEvent(e: InventoryClickEvent) : Boolean {
        val inventoryName = e.view.title.split(" ")
        if (inventoryName[0] == "Kit" && e.currentItem != null) {
            e.isCancelled = true
            val number = e.slot
            if (number == 27 && e.currentItem != null) {
                e.whoClicked.openInventory(kitGuiCache.getIfPresent(1)!!)
            }
            return true
        }
        if (inventoryName[0] == "Kits") {
            e.isCancelled = true
            val number = e.slot
            if (number < 27) {
                val kit = kitClickGuiCache.getIfPresent((number + 1) + ((inventoryName[1].toInt() - 1) * 27))
                kitGui(kit!!, e.whoClicked as Player)
            }
            else {
                if (number == 27 && inventoryName[1].toInt() > 1) {
                    e.whoClicked.openInventory(kitGuiCache.getIfPresent(inventoryName[1].toInt() - 1)!!)
                }
                if (number == 35) {
                    val check = kitGuiCache.getIfPresent(inventoryName[1].toInt() + 1)
                    if (check != null) {
                        e.whoClicked.openInventory(check)
                    }
                }
            }
            return true
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
                Kit().reloadKitCache()
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
                            kitsCache.put(kit, CompletableFuture.supplyAsync { KSystemKit(kit, kitTime, kitRealName, convertItems(item))})
                        }
                    }
                    KitsInventory().kitGuiInventory()
                    Dao.inUpdate = false
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