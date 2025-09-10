package github.gilbertokpl.total.cache.serializer

import github.gilbertokpl.core.cache.interfaces.ICacheSerializer

internal class CommandsSerializer : ICacheSerializer<ArrayList<String>, String> {
    override fun convertToDatabase(hash: ArrayList<String>): String {
        return hash.joinToString("|")
    }

    override fun convertToCache(value: String): ArrayList<String> {
        if (value.isBlank()) {
            return ArrayList()
        }
        return ArrayList(value.split("|"))
    }
}