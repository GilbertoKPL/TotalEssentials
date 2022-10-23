package github.gilbertokpl.total.serializer

import github.gilbertokpl.base.external.cache.convert.SerializatorBase
import org.bukkit.Location

internal class HomeSerializer : SerializatorBase<HashMap<String, Location>, String> {

    private val locationSerializer = LocationSerializer()
    override fun convertToDatabase(hash: HashMap<String, Location>): String {
        var string = ""
        for (i in hash) {
            val toString = "${i.key},${locationSerializer.convertToDatabase(i.value)}"
            string += if (string == "") {
                toString
            } else {
                "|$toString"
            }
        }
        return string
    }

    override fun convertToCache(value: String): HashMap<String, Location> {
        val hash = HashMap<String, Location>()
        for (i in value.split("|")) {
            val split = i.split(",")
            if (split.size < 2) continue
            hash[split[0]] = locationSerializer.convertToCache(split[1]) ?: continue
        }
        return hash
    }
}