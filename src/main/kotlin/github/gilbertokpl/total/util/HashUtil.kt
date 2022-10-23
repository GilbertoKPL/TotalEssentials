package github.gilbertokpl.total.util


internal object HashUtil {

    fun hashMapSortMap(passedMap: HashMap<String, Int>): LinkedHashMap<String, Int> {
        val mapKeys: ArrayList<String> = ArrayList(passedMap.keys)
        val mapValues: ArrayList<Int> = ArrayList(passedMap.values)
        mapValues.sort()
        mapKeys.sort()
        val sortedMap: LinkedHashMap<String, Int> = LinkedHashMap()
        val valueIt = mapValues.iterator()
        while (valueIt.hasNext()) {
            val t = valueIt.next()
            val keyIt = mapKeys.iterator()
            while (keyIt.hasNext()) {
                val key = keyIt.next()
                val comp1 = passedMap[key].toString()
                val comp2 = t.toString()
                if (comp1 == comp2) {
                    passedMap.remove(key)
                    mapKeys.remove(key)
                    sortedMap[key] = t
                    break
                }
            }
        }
        return sortedMap
    }

    fun hashMapReverse(map: LinkedHashMap<String, Int>): LinkedHashMap<String, Int> {
        val reversedMap = LinkedHashMap<String, Int>()
        val it: ListIterator<Map.Entry<String, Int>> =
            ArrayList<Map.Entry<String, Int>>(map.entries).listIterator(map.entries.size)
        while (it.hasPrevious()) {
            val (key, value) = it.previous()
            reversedMap[key] = value
        }
        return reversedMap
    }
}
