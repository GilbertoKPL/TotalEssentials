package github.gilbertokpl.essentialsk.serializator.internal

import github.gilbertokpl.essentialsk.data.dao.KitData

internal object KitSerializer {
    fun serialize(hash: HashMap<String, Long>): String {
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

    fun deserialize(string: String): HashMap<String, Long> {
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
}