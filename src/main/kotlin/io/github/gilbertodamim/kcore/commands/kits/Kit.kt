package io.github.gilbertodamim.kcore.commands.kits

import com.github.benmanes.caffeine.cache.Caffeine
import io.github.gilbertodamim.kcore.KCoreMain
import io.github.gilbertodamim.kcore.KCoreMain.pluginName
import io.github.gilbertodamim.kcore.config.configs.KitsConfig.useShortTime
import io.github.gilbertodamim.kcore.config.langs.GeneralLang.notPerm
import io.github.gilbertodamim.kcore.config.langs.GeneralLang.onlyPlayerCommand
import io.github.gilbertodamim.kcore.config.langs.KitsLang.*
import io.github.gilbertodamim.kcore.database.SqlInstance
import io.github.gilbertodamim.kcore.database.table.PlayerKits
import io.github.gilbertodamim.kcore.database.table.SqlKits
import io.github.gilbertodamim.kcore.inventory.Api
import io.github.gilbertodamim.kcore.inventory.KitsInventory
import io.github.gilbertodamim.kcore.management.ErrorClass
import io.github.gilbertodamim.kcore.management.Manager.convertMillisToString
import io.github.gilbertodamim.kcore.management.Manager.getPlayerUUID
import io.github.gilbertodamim.kcore.management.dao.Dao
import io.github.gilbertodamim.kcore.management.dao.Dao.kitClickGuiCache
import io.github.gilbertodamim.kcore.management.dao.Dao.kitGuiCache
import io.github.gilbertodamim.kcore.management.dao.Dao.kitPlayerCache
import io.github.gilbertodamim.kcore.management.dao.Dao.kitsCache
import io.github.gilbertodamim.kcore.management.dao.KCoreKit
import io.github.gilbertodamim.kcore.management.serialize.KCoreBukkitObjectInputStream
import io.github.gilbertodamim.kcore.management.serialize.KCoreBukkitObjectOutputStream
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
        if (args.isNotEmpty() && s.hasPermission("kcore.kits")) {
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
            } else {
                s.sendMessage(notExistKits)
            }
        }
        return false
    }

    private fun kitList(): List<String> {
        val list = ArrayList<String>()
        for (i in kitsCache.asMap()) {
            list.add(i.key)
        }
        return list
    }

    private fun startPlayerKitCache() {
        kitPlayerCache.invalidateAll()
        CompletableFuture.runAsync({
            try {
                transaction(SqlInstance.SQL) {
                    for (i in PlayerKits.selectAll()) {
                        var toPut = ""
                        var used = false
                        val cache = Caffeine.newBuilder().maximumSize(500).build<String, Long>()
                        for (a in i[PlayerKits.kitsTime].split("-")) {
                            val split = a.split(".")
                            val long = split[1].toLong()
                            val name = split[0]
                            val timeAll = SqlKits.select { SqlKits.kitName eq name }.single()[SqlKits.kitTime] + long
                            if (long != 0L || timeAll > System.currentTimeMillis()) {
                                if (toPut == "") {
                                    toPut = a
                                } else {
                                    toPut += "-$a"
                                }
                                used = true
                                cache.put(name, long)
                            }
                        }
                        PlayerKits.update({ PlayerKits.uuid eq i[PlayerKits.uuid] }) {
                            it[kitsTime] = toPut
                        }
                        if (!used) {
                            PlayerKits.deleteWhere { PlayerKits.uuid eq i[PlayerKits.uuid] }
                        } else {
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
            cache.put(kit, System.currentTimeMillis())
            kitPlayerCache.invalidate(pUUID)
            kitPlayerCache.put(pUUID, cache)
        } else {
            val newCache = Caffeine.newBuilder().maximumSize(500).build<String, Long>()
            for (i in kitsCache.asMap()) {
                val name = i.value.name
                if (name == kit) continue
                newCache.put(name, 0L)
            }
            newCache.put(kit, System.currentTimeMillis())
            kitPlayerCache.put(pUUID, newCache)
        }
        CompletableFuture.runAsync({
            try {
                transaction(SqlInstance.SQL) {
                    val work = PlayerKits.select { PlayerKits.uuid eq pUUID }
                    if (work.empty()) {
                        PlayerKits.insert {
                            it[uuid] = pUUID
                            it[kitsTime] = "$kit.${System.currentTimeMillis()}"
                        }
                    } else {
                        val to = work.single()[PlayerKits.kitsTime]
                        PlayerKits.update({ PlayerKits.uuid eq pUUID }) {
                            it[kitsTime] = "$to-$kit.${System.currentTimeMillis()}"
                        }
                    }
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
                timeAll = kitCache.time + timePlayerPickedKit
            }
            if (timeAll == 0L || timeAll <= System.currentTimeMillis()) {
                if (p.hasPermission("kcore.kit.$kit")) {
                    for (i in kitCache.items) {
                        if (i == null) continue
                        p.inventory.addItem(i)
                    }
                    p.sendMessage(pickedSuccess.replace("%kit%", kitCache.realName))
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
        val inv = KCoreMain.instance.server.createInventory(null, 45, "$pluginName§eKit $kit $guiNumber")
        val to = kitsCache.getIfPresent(kit)!!
        val pUUID = getPlayerUUID(p)
        val timePlayerPickedKit = kitPlayerCache.getIfPresent(pUUID)?.getIfPresent(kit)
        var timeAll = 0L
        if (timePlayerPickedKit != null) {
            timeAll = kitsCache.getIfPresent(kit)?.time!! + timePlayerPickedKit
        }
        for (i in to.items) {
            if (i == null) continue
            inv.addItem(i)
        }
        for (to1 in 36..44) {
            if (to1 == 36) {
                inv.setItem(to1, Api().item(Material.HOPPER, kitInventoryIconBackName, true))
                continue
            }
            if (to1 == 40) {
                if (p.hasPermission("kcore.kits.admin")) {
                    inv.setItem(to1, Api().item(Material.CHEST, kitInventoryIconEditKitName, true))
                    continue
                }
            }
            if (to1 == 44) {
                if (p.hasPermission("kcore.kit.$kit")) {
                    if (timeAll <= System.currentTimeMillis() || timeAll == 0L) {
                        inv.setItem(to1, Api().item(Material.ARROW, kitPickupIcon, true))
                    } else {
                        val array = ArrayList<String>()
                        val remainingTime = timeAll - System.currentTimeMillis()
                        for (i in kitPickupIconLoreTime) {
                            array.add(i.replace("%time%", convertMillisToString(remainingTime, useShortTime)))
                        }
                        inv.setItem(to1, Api().item(Material.ARROW, kitPickupIconNotPickup, array, true))
                    }
                } else {
                    inv.setItem(to1, Api().item(Material.ARROW, kitPickupIconNotPickup, kitPickupIconLoreNotPerm, true))
                }
                continue
            }
            inv.setItem(
                to1,
                Api().item(Dao.Materials["glass"]!!, "${pluginName}§eKIT", true)
            )
        }
        p.openInventory(inv)
    }

    fun kitGuiEvent(e: InventoryClickEvent): Boolean {
        val inventoryName = e.view.title.split(" ")
        if (inventoryName[0] == "$pluginName§eKit" && e.currentItem != null) {
            val p = e.whoClicked as Player
            e.isCancelled = true
            val number = e.slot
            if (number == 36 && e.currentItem != null) {
                p.openInventory(kitGuiCache.getIfPresent(inventoryName[2].toInt())!!)
            }
            if (number == 40 && e.currentItem != null && e.currentItem!!.itemMeta != null && e.currentItem!!.itemMeta?.displayName == kitInventoryIconEditKitName) {
                if (p.hasPermission("kcore.kits.admin")) {
                    EditKit().editKitGui(inventoryName[1], p)
                }
            }
            if (number == 44 && e.currentItem!!.itemMeta != null) {
                if (e.currentItem!!.itemMeta?.displayName == kitPickupIcon) {
                    pickupKit(p, inventoryName[1])
                    p.closeInventory()
                }
                if (e.currentItem!!.itemMeta?.displayName == kitPickupIconNotPickup) {
                    kitGui(inventoryName[1], inventoryName[2], p)
                }
            }
            return true
        }
        if (inventoryName[0] == "$pluginName§eKits") {
            val p = e.whoClicked as Player
            e.isCancelled = true
            val number = e.slot
            if (number < 27) {
                val kit = kitClickGuiCache.getIfPresent((number + 1) + ((inventoryName[1].toInt() - 1) * 27))
                if (kit != null) {
                    kitGui(kit, inventoryName[1], p)
                }
            } else {
                if (number == 27 && inventoryName[1].toInt() > 1) {
                    p.openInventory(kitGuiCache.getIfPresent(inventoryName[1].toInt() - 1)!!)
                }
                if (number == 35) {
                    val check = kitGuiCache.getIfPresent(inventoryName[1].toInt() + 1)
                    if (check != null) {
                        p.openInventory(check)
                    }
                }
            }
            return true
        }
        return false
    }

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
                kitsCache.invalidateAll()
                transaction(SqlInstance.SQL) {
                    for (values in SqlKits.selectAll()) {
                        val kit = values[SqlKits.kitName]
                        val kitRealName = values[SqlKits.kitRealName]
                        val kitTime = values[SqlKits.kitTime]
                        val item = values[SqlKits.kitItems]
                        kitsCache.put(
                            kit,
                            KCoreKit(
                                kit,
                                kitTime,
                                kitRealName,
                                convertItems(item)
                            )
                        )
                    }
                }
                KitsInventory().kitGuiInventory()
            } catch (ex: Exception) {
                ErrorClass().sendException(ex)
            }
        }, Executors.newCachedThreadPool())
    }

    fun convertItems(items: Array<ItemStack?>): String {
        var toReturn = ""
        try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = try {
                BukkitObjectOutputStream(outputStream)
            } catch (e: NoClassDefFoundError) {
                KCoreBukkitObjectOutputStream(outputStream)
            }
            dataOutput.writeInt(items.size)
            for (i in items.indices) {
                dataOutput.writeObject(items[i])
            }
            dataOutput.close()
            toReturn = Base64Coder.encodeLines(outputStream.toByteArray())
        } catch (e: ClassNotFoundException) {
        }
        return toReturn
    }

    @Throws(IOException::class)
    fun convertItems(data: String): Array<ItemStack?> {
        return try {
            val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
            val dataInput = try {
                BukkitObjectInputStream(inputStream)
            } catch (e: NoClassDefFoundError) {
                KCoreBukkitObjectInputStream(inputStream)
            }
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