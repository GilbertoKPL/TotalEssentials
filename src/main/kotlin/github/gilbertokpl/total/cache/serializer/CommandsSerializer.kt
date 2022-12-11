package github.gilbertokpl.total.cache.serializer

import github.gilbertokpl.core.external.cache.convert.SerializatorBase

internal class CommandsSerializer : SerializatorBase<ArrayList<String>, String> {
    override fun convertToDatabase(hash: ArrayList<String>): String {
        var h = ""
        for (i in hash) {
            if (h == "") {
                h += i
                continue
            }
            h += "|$i"
        }
        return h
    }

    override fun convertToCache(value: String): ArrayList<String> {
        val split = value.split("|")

        val list = ArrayList<String>()

        for (i in split) {
            list.add(i)
        }

        return list
    }
}