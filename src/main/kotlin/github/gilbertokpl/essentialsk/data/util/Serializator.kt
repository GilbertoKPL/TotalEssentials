package github.gilbertokpl.essentialsk.data.util

import github.gilbertokpl.essentialsk.data.dao.KitData
import github.gilbertokpl.essentialsk.util.LocationUtil
import org.bukkit.Location

object Serializator {

    fun playerDataKit(hash: HashMap<String, Long>) : String {
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

    fun playerDataKit(string : String) : HashMap<String, Long> {
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

    fun playerDataHome(hash: HashMap<String, Location>) : String {
        var string = ""
        for (i in hash) {
            val toString = "${i.key},${LocationUtil.locationSerializer(i.value)}"
            string += if (string == "") {
                toString
            } else {
                "|$toString"
            }
        }
        return string
    }

    fun playerDataHome(string : String) : HashMap<String, Location> {
        val hash = HashMap<String, Location>()
        for (i in string.split("|")) {
            val split = i.split(",")
            if (split.size < 2) continue
            hash[split[0]] = LocationUtil.locationSerializer(split[1]) ?: continue
        }
        return hash
    }
}