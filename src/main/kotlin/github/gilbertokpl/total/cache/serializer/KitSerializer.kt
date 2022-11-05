package github.gilbertokpl.total.cache.serializer

import github.gilbertokpl.core.external.cache.convert.SerializatorBase
import github.gilbertokpl.total.cache.local.KitsData

internal class KitSerializer : SerializatorBase<HashMap<String, Long>, String> {
    override fun convertToDatabase(hash: HashMap<String, Long>): String {
        var string = ""

        for (i in hash) {
            if (KitsData.checkIfExist(i.key)) {
                val timeAll = KitsData.kitTime[i.key] ?: continue
                if ((i.value != 0L) && ((timeAll + i.value) > System.currentTimeMillis())) {
                    val toString = "${i.key},${i.value}"
                    string += if (string == "") {
                        toString
                    } else {
                        "|$toString"
                    }
                }
                continue
            }
        }
        return string
    }

    override fun convertToCache(value: String): HashMap<String, Long> {
        val hash = HashMap<String, Long>()
        for (i in value.split("|")) {
            val split = i.split(",")
            if (split.size < 2) continue
            val nameKit = split[0].lowercase()
            val timeKit = split[1].toLong()

            hash[nameKit] = timeKit

        }

        return hash
    }
}