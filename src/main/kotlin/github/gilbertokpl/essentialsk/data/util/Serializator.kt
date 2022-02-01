package github.gilbertokpl.essentialsk.data.util

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.data.dao.KitData
import github.gilbertokpl.essentialsk.manager.InternalBukkitObjectInputStream
import github.gilbertokpl.essentialsk.manager.InternalBukkitObjectOutputStream
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

internal object Serializator {

    fun playerDataKit(hash: HashMap<String, Long>): String {
        var string = ""
        for (i in hash) {
            val toString = "${i.key},${i.value}"
            string += if (string == "") {
                toString
            } else {
                "|$toString"
            }
        }
        return string
    }

    fun playerDataKit(string: String): HashMap<String, Long> {
        val hash = HashMap<String, Long>()
        for (i in string.split("|")) {
            val split = i.split(",")
            if (split.size < 2) continue
            val nameKit = split[0].lowercase()
            val timeKit = split[1].toLong()
            val kitsCache = KitData[nameKit]

            if (kitsCache != null) {
                val timeAll = kitsCache.timeCache
                if ((timeKit != 0L) && ((timeAll + timeKit) > System.currentTimeMillis())) {
                    hash[nameKit] = timeKit
                }
                continue
            }
        }
        return hash
    }

    fun playerDataHome(hash: HashMap<String, Location>): String {
        var string = ""
        for (i in hash) {
            val toString = "${i.key},${locationSerializer(i.value)}"
            string += if (string == "") {
                toString
            } else {
                "|$toString"
            }
        }
        return string
    }

    fun playerDataHome(string: String): HashMap<String, Location> {
        val hash = HashMap<String, Location>()
        for (i in string.split("|")) {
            val split = i.split(",")
            if (split.size < 2) continue
            hash[split[0]] = locationSerializer(split[1]) ?: continue
        }
        return hash
    }

    fun locationSerializer(loc: Location?): String {
        loc ?: return ""
        return loc.x.toString() + ";" +
                loc.y.toString() + ";" +
                loc.z.toString() + ";" +
                loc.world?.name + ";" +
                loc.pitch + ";" +
                loc.yaw
    }

    fun locationSerializer(s: String): Location? {
        return try {
            val parts = s.split(";").toTypedArray()
            val x = parts[0].toDouble()
            val y = parts[1].toDouble()
            val z = parts[2].toDouble()
            val w = try {
                EssentialsK.instance.server.getWorld(parts[3])
            } catch (e: Throwable) {
                null
            }
            Location(
                w, x, y, z, try {
                    parts[5].toFloat()
                } catch (e: Throwable) {
                    0F
                }, try {
                    parts[4].toFloat()
                } catch (e: Throwable) {
                    0F
                }
            )
        } catch (e: Throwable) {
            null
        }
    }

    fun itemSerializer(items: List<ItemStack>): String {
        if (items.isEmpty()) return ""
        lateinit var toReturn: String
        try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = try {
                BukkitObjectOutputStream(outputStream)
            } catch (e: NoClassDefFoundError) {
                InternalBukkitObjectOutputStream(outputStream)
            }
            dataOutput.writeInt(items.size)
            for (i in items.indices) {
                dataOutput.writeObject(items[i])
            }
            dataOutput.close()
            toReturn = Base64Coder.encodeLines(outputStream.toByteArray())
        } catch (e: Throwable) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
        }
        return toReturn
    }

    fun itemSerializer(data: String): List<ItemStack> {
        if (data == "") return emptyList()
        lateinit var toReturn: List<ItemStack>
        try {
            val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
            val dataInput = try {
                BukkitObjectInputStream(inputStream)
            } catch (e: NoClassDefFoundError) {
                InternalBukkitObjectInputStream(inputStream)
            }
            val items = arrayOfNulls<ItemStack>(dataInput.readInt())
            for (i in items.indices) {
                items[i] = dataInput.readObject() as ItemStack?
            }
            dataInput.close()
            toReturn = items.filterNotNull().toList()
        } catch (e: Throwable) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
        }
        return toReturn
    }
}