package github.gilbertokpl.core.cache.interfaces

interface ICacheSerializer<T, V> {

    fun convertToDatabase(hash: T): V

    fun convertToCache(value: V): T?

}