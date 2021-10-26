package io.github.gilbertodamim.ksystem.commands.kits

import com.github.benmanes.caffeine.cache.Caffeine
import io.github.gilbertodamim.ksystem.KSystemMain
import io.github.gilbertodamim.ksystem.config.configs.KitsConfig.useShortTime
import io.github.gilbertodamim.ksystem.config.langs.GeneralLang.notPerm
import io.github.gilbertodamim.ksystem.config.langs.GeneralLang.onlyPlayerCommand
import io.github.gilbertodamim.ksystem.config.langs.KitsLang
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.kitPickupIcon
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.kitPickupIconLoreNotPerm
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.kitPickupIconLoreTime
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.kitPickupMessage
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.notExist
import io.github.gilbertodamim.ksystem.database.SqlInstance
import io.github.gilbertodamim.ksystem.database.table.PlayerKits
import io.github.gilbertodamim.ksystem.database.table.SqlKits
import io.github.gilbertodamim.ksystem.inventory.Api
import io.github.gilbertodamim.ksystem.inventory.KitsInventory
import io.github.gilbertodamim.ksystem.management.Manager.convertMillisToString
import io.github.gilbertodamim.ksystem.management.Manager.getPlayerUUID
import io.github.gilbertodamim.ksystem.management.dao.Dao
import io.github.gilbertodamim.ksystem.management.dao.Dao.kitClickGuiCache
import io.github.gilbertodamim.ksystem.management.dao.Dao.kitGuiCache
import io.github.gilbertodamim.ksystem.management.dao.Dao.kitPlayerCache
import io.github.gilbertodamim.ksystem.management.dao.Dao.kitsCache
import io.github.gilbertodamim.ksystem.management.dao.KSystemKit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
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
            val to = kitsCache.getIfPresent(args[0].lowercase())
            if (to != null) {
                for (i in to.get().items) {
                    s.player?.inventory?.addItem(i ?: continue)
                }
            }
            else {
                //send kitList
            }
        } else {
            val inv = kitGuiCache.getIfPresent(1)
            if (inv != null) {
                s.openInventory(inv)
            }
        }
        return false
    }

    private fun reloadKitCache() {
        kitPlayerCache.cleanUp()
        CompletableFuture.runAsync({
            try {
                transaction(SqlInstance.SQL) {
                    for (i in PlayerKits.selectAll()) {
                        val cache = Caffeine.newBuilder().maximumSize(500).build<String, Long>()
                        for (col in PlayerKits.columns) {
                            if (col == PlayerKits.uuid) continue
                            cache.put(col.name, i[col] as Long)
                        }
                        kitPlayerCache.put(i[PlayerKits.uuid], cache)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Executors.newSingleThreadExecutor())
    }

    private fun upDatePlayerKitTime(p: Player, kit: String) {
        val pUUID = getPlayerUUID(p)
        val cache = kitPlayerCache.getIfPresent(pUUID)
        if (cache != null) {
            cache.invalidate(kit)
            cache.put(kit, System.currentTimeMillis())
            kitPlayerCache.invalidate(pUUID)
            kitPlayerCache.put(pUUID, cache)
        }
        CompletableFuture.runAsync({
            try {
                transaction(SqlInstance.SQL) {
                    val work = PlayerKits.select { PlayerKits.uuid eq pUUID }
                    val col = Column<Long>(PlayerKits, kit, LongColumnType())
                    if (work.empty()) {
                        PlayerKits.insert {
                            it[uuid] = pUUID
                            it[col] = System.currentTimeMillis()
                        }
                        addPlayerToCache(p)
                    } else {
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

    private fun addPlayerToCache(p: Player) {
        CompletableFuture.runAsync({
            try {
                val pUUID = getPlayerUUID(p)
                transaction(SqlInstance.SQL) {
                    val cache = Caffeine.newBuilder().maximumSize(500).build<String, Long>()
                    val work = PlayerKits.select { PlayerKits.uuid eq pUUID }
                    if (work.empty()) {
                        PlayerKits.insert {
                            it[uuid] = pUUID
                        }
                    }
                    for (i in PlayerKits.columns) {
                        if (i == PlayerKits.uuid) continue
                        cache.put(i.name, work.single()[i] as Long)
                    }
                    kitPlayerCache.put(pUUID, cache)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Executors.newSingleThreadExecutor())
    }

    private fun pickupKit(p: Player, kit: String) {
        val pUUID = getPlayerUUID(p)
        val kitCache = kitsCache.getIfPresent(kit)
        if (kitCache != null) {
            val timePlayerPickedKit = kitPlayerCache.getIfPresent(pUUID)?.getIfPresent(kit)
            var timeAll = 0L
            if (timePlayerPickedKit != null) {
                timeAll = kitCache.get()?.time!! + timePlayerPickedKit
            }
            if (timeAll == 0L || timeAll < System.currentTimeMillis()) {
                if (p.hasPermission("ksystem.kit.$kit")) {
                    for (i in kitCache.get()?.items!!) {
                        if (i == null) continue
                        p.inventory.addItem(i)
                    }
                    upDatePlayerKitTime(p, kit)
                } else {
                    p.sendMessage(notPerm)
                }
            } else {
                val remainingTime = timeAll - System.currentTimeMillis()
                p.sendMessage(kitPickupMessage.replace("%time%", convertMillisToString(remainingTime, useShortTime)))
            }
        } else {
            p.sendMessage(notExist)
        }
    }

    private fun kitGui(kit: String, p: Player) {
        val inv = KSystemMain.instance.server.createInventory(null, 36, "Kit $kit")
        val to = kitsCache.getIfPresent(kit)!!.get()
        val pUUID = getPlayerUUID(p)
        val timePlayerPickedKit = kitPlayerCache.getIfPresent(pUUID)?.getIfPresent(kit)
        var timeAll = 0L
        if (timePlayerPickedKit != null) {
            timeAll = kitsCache.getIfPresent(kit)?.get()?.time!! + timePlayerPickedKit
        }
        for (i in to.items) {
            if (i == null) continue
            inv.addItem(i)
        }
        inv.setItem(27, Api.item(Material.HOPPER, KitsLang.kitInventoryIconBackName))
        if (p.hasPermission("ksystem.kit.$kit")) {
            if (timeAll < System.currentTimeMillis() || timeAll == 0L) {
                inv.setItem(35, Api.item(Material.ARROW, kitPickupIcon))
            } else {
                val array = ArrayList<String>()
                val remainingTime = timeAll - System.currentTimeMillis()
                for (i in kitPickupIconLoreTime) {
                    array.add(i.replace("%time%", convertMillisToString(remainingTime, useShortTime)))
                }
                inv.setItem(35, Api.item(Material.ARROW, KitsLang.kitPickupIconNotPickup, array))
            }
        } else {
            inv.setItem(35, Api.item(Material.ARROW, KitsLang.kitPickupIconNotPickup, kitPickupIconLoreNotPerm))
        }
        p.openInventory(inv)
    }

    fun kitGuiEvent(e: InventoryClickEvent): Boolean {
        val inventoryName = e.view.title.split(" ")
        if (inventoryName[0] == "Kit" && e.currentItem != null) {
            e.isCancelled = true
            val number = e.slot
            if (number == 27 && e.currentItem != null) {
                e.whoClicked.openInventory(kitGuiCache.getIfPresent(1)!!)
            }
            if (number == 35 && e.currentItem!!.itemMeta != null && e.currentItem!!.itemMeta?.displayName == kitPickupIcon) {
                pickupKit(e.whoClicked as Player, inventoryName[1])
                e.whoClicked.closeInventory()
            }
            return true
        }
        if (inventoryName[0] == "Kits") {
            e.isCancelled = true
            val number = e.slot
            if (number < 27) {
                val kit = kitClickGuiCache.getIfPresent((number + 1) + ((inventoryName[1].toInt() - 1) * 27))
                if (kit != null) {
                    kitGui(kit, e.whoClicked as Player)
                }
            } else {
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
                            kitsCache.put(
                                kit,
                                CompletableFuture.supplyAsync {
                                    KSystemKit(
                                        kit,
                                        kitTime,
                                        kitRealName,
                                        convertItems(item)
                                    )
                                })
                        }
                    }
                    KitsInventory().kitGuiInventory()
                    Dao.inUpdate = false
                } catch (e: Exception) {
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