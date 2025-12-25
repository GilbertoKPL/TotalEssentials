package github.gilbertokpl.total.cache.serializer

import github.gilbertokpl.core.cache.interfaces.ICacheSerializer

class VipSerializer : ICacheSerializer<HashMap<String, Long>, String> {
    override fun convertToDatabase(hash: HashMap<String, Long>): String {
        return hash.entries
            .sortedBy { it.key }
            .joinToString("|") { (key, value) ->
                "$key,$value"
            }
    }

    override fun convertToCache(value: String): HashMap<String, Long> {
        val hash = HashMap<String, Long>()
        for (i in value.split("|")) {
            val split = i.split(",")
            if (split.size < 2) continue
            hash[split[0]] = split[1].toLong()
        }
        return hash
    }
}