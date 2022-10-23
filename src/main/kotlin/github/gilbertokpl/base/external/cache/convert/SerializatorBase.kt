package github.gilbertokpl.base.external.cache.convert

interface SerializatorBase<T, V> {
    fun convertToDatabase(hash: T): V
    fun convertToCache(value: V): T
}