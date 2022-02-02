package github.gilbertokpl.essentialsk.serializator.internal

import org.bukkit.Location

internal object HomeSerializer {
    fun serialize(hash: HashMap<String, Location>): String {
        var string = ""
        for (i in hash) {
            val toString = "${i.key},${LocationSerializer.serialize(i.value)}"
            string += if (string == "") {
                toString
            } else {
                "|$toString"
            }
        }
        return string
    }

    fun deserialize(string: String): HashMap<String, Location> {
        val hash = HashMap<String, Location>()
        for (i in string.split("|")) {
            val split = i.split(",")
            if (split.size < 2) continue
            hash[split[0]] = LocationSerializer.deserialize(split[1]) ?: continue
        }
        return hash
    }
}