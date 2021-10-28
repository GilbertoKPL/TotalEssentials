package io.github.gilbertodamim.ksystem.commands.kits

import com.github.benmanes.caffeine.cache.Caffeine
import io.github.gilbertodamim.ksystem.KSystemMain
import io.github.gilbertodamim.ksystem.KSystemMain.pluginName
import io.github.gilbertodamim.ksystem.config.configs.KitsConfig.useShortTime
import io.github.gilbertodamim.ksystem.config.langs.GeneralLang.notPerm
import io.github.gilbertodamim.ksystem.config.langs.GeneralLang.onlyPlayerCommand
import io.github.gilbertodamim.ksystem.config.langs.KitsLang
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.kitInventoryIconEditkitName
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.kitPickupIcon
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.kitPickupIconLoreNotPerm
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.kitPickupIconLoreTime
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.kitPickupIconNotPickup
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.kitPickupMessage
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.list
import io.github.gilbertodamim.ksystem.config.langs.KitsLang.notExist
import io.github.gilbertodamim.ksystem.database.SqlInstance
import io.github.gilbertodamim.ksystem.database.table.PlayerKits
import io.github.gilbertodamim.ksystem.database.table.SqlKits
import io.github.gilbertodamim.ksystem.inventory.Api
import io.github.gilbertodamim.ksystem.inventory.KitsInventory
import io.github.gilbertodamim.ksystem.management.ErrorClass
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
                pickupKit(s, args[0].lowercase())
            } else {
                s.sendMessage(list.replace("%kits%", kitList().toString()))
            }
        } else {
            val inv = kitGuiCache.getIfPresent(1)
            if (inv != null) {
                s.openInventory(inv)
            }
            else {
                s.sendMessage(KitsLang.notExistKits)
            }
        }
        return false
    }

    private fun kitList() : List<String> {
        val list = ArrayList<String>()
        for(i in kitsCache.asMap()) {
            list.add(i.key)
        }
        return list
    }

    private fun startPlayerKitCache() {
        kitPlayerCache.cleanUp()
        CompletableFuture.runAsync({
            try {
                transaction(SqlInstance.SQL) {
                    for (i in PlayerKits.selectAll()) {
                        val sizeColumn = PlayerKits.columns.size - 1
                        var checkSizeColumn = 0
                        val cache = Caffeine.newBuilder().maximumSize(500).build<String, Long>()
                        for (col in PlayerKits.columns) {
                            if (col == PlayerKits.uuid) continue
                            val long = i[col] as Long
                            val timeAll = kitsCache.getIfPresent(col.name)?.get()?.time!! + long
                            if (long == 0L || timeAll < System.currentTimeMillis()) {
                                checkSizeColumn += 1
                            }
                            cache.put(col.name, long)
                        }
                        if (sizeColumn == checkSizeColumn) {
                            PlayerKits.deleteWhere { PlayerKits.uuid eq i[PlayerKits.uuid]}
                        }
                        else {
                            kitPlayerCache.put(i[PlayerKits.uuid], cache)
                        }
                    }
                }
            } catch (ex: Exception) {
                ErrorClass().sendException(ex)
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
                    val array = ArrayList<Column<Long>>()
                    for (column in SqlKits.selectAll()) {
                        val colTo = Column<Long>(PlayerKits, column[SqlKits.kitName], LongColumnType())
                        array.add(colTo)
                    }
                    if (work.empty()) {
                        PlayerKits.insert {
                            it[uuid] = pUUID
                            it[col] = System.currentTimeMillis()
                            for (i in array) {
                                if (i == uuid) continue
                                if (i == col) continue
                                val colTo = Column<Long>(PlayerKits, i.name, LongColumnType())
                                it[colTo] = 0L
                            }
                        }
                        addPlayerToCache(p)
                    } else {
                        PlayerKits.update({ PlayerKits.uuid eq pUUID }) {
                            it[col] = System.currentTimeMillis()
                        }
                    }
                }
            } catch (ex: Exception) {
                ErrorClass().sendException(ex)
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
            } catch (ex: Exception) {
                ErrorClass().sendException(ex)
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
            if (timeAll == 0L || timeAll <= System.currentTimeMillis()) {
                if (p.hasPermission("ksystem.kit.$kit")) {
                    for (i in kitCache.get()?.items!!) {
                        if (i == null) continue
                        p.inventory.addItem(i)
                    }
                    p.sendMessage(KitsLang.pickedSuccess.replace("%kit%", kitCache.get().realName))
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

    private fun kitGui(kit: String, guiNumber: String, p: Player) {
        val inv = KSystemMain.instance.server.createInventory(null, 36, "$pluginName§eKit $kit $guiNumber")
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
        for (to1 in 27..35) {
            if (to1 == 27) {
                inv.setItem(27, Api.item(Material.HOPPER, KitsLang.kitInventoryIconBackName, true))
                continue
            }
            if (to1 == 31) {
                if (p.hasPermission("ksystem.kits.admin")) {
                    inv.setItem(to1, Api.item(Material.CHEST, kitInventoryIconEditkitName, true))
                    continue
                }
            }
            if (to1 == 35) {
                if (p.hasPermission("ksystem.kit.$kit")) {
                    if (timeAll <= System.currentTimeMillis() || timeAll == 0L) {
                        inv.setItem(to1, Api.item(Material.ARROW, kitPickupIcon, true))
                    } else {
                        val array = ArrayList<String>()
                        val remainingTime = timeAll - System.currentTimeMillis()
                        for (i in kitPickupIconLoreTime) {
                            array.add(i.replace("%time%", convertMillisToString(remainingTime, useShortTime)))
                        }
                        inv.setItem(to1, Api.item(Material.ARROW, kitPickupIconNotPickup, array , true))
                    }
                } else {
                    inv.setItem(to1, Api.item(Material.ARROW, kitPickupIconNotPickup, kitPickupIconLoreNotPerm , true))
                }
                continue
            }
            inv.setItem(to1,
                Api.item(Dao.Materials["glass"]!!, "${pluginName}§eKIT", true)
            )
        }
        p.openInventory(inv)
    }

    fun kitGuiEvent(e: InventoryClickEvent): Boolean {
        val inventoryName = e.view.title.split(" ")
        if (inventoryName[0] == "$pluginName§eKit" && e.currentItem != null) {
            e.isCancelled = true
            val number = e.slot
            if (number == 27 && e.currentItem != null) {
                e.whoClicked.openInventory(kitGuiCache.getIfPresent(inventoryName[2].toInt())!!)
            }
            if (number == 31 && e.currentItem != null && e.currentItem!!.itemMeta != null && e.currentItem!!.itemMeta?.displayName == kitInventoryIconEditkitName) {
                if (e.whoClicked.hasPermission("ksystem.kits.admin")) {
                    EditKit().editKitGui(inventoryName[1], e.whoClicked as Player)
                }
            }
            if (number == 35 && e.currentItem!!.itemMeta != null) {
                if (e.currentItem!!.itemMeta?.displayName == kitPickupIcon) {
                    pickupKit(e.whoClicked as Player, inventoryName[1])
                    e.whoClicked.closeInventory()
                }
                if (e.currentItem!!.itemMeta?.displayName == kitPickupIconNotPickup) {
                    kitGui(inventoryName[1], inventoryName[2], e.whoClicked as Player)
                }
            }
            return true
        }
        if (inventoryName[0] == "$pluginName§eKits") {
            e.isCancelled = true
            val number = e.slot
            if (number < 27) {
                val kit = kitClickGuiCache.getIfPresent((number + 1) + ((inventoryName[1].toInt() - 1) * 27))
                if (kit != null) {
                    kitGui(kit, inventoryName[1], e.whoClicked as Player)
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
                startCacheKits()
                Kit().startPlayerKitCache()
            }
        }

        private fun startCacheKits() {
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
                } catch (ex: Exception) {
                    ErrorClass().sendException(ex)
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