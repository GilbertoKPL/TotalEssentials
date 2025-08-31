package github.gilbertokpl.core.cache.convert

interface SerializerBase<T, V> {
    fun convertToDatabase(hash: T): V
    fun convertToCache(value: V): T?
}