package github.gilbertokpl.base.external.cache

import github.gilbertokpl.base.external.BasePlugin
import github.gilbertokpl.base.external.cache.convert.SerializatorBase
import github.gilbertokpl.base.external.cache.interfaces.CacheBase
import github.gilbertokpl.base.external.cache.interfaces.CacheBuilder
import github.gilbertokpl.base.external.cache.interfaces.CacheBuilderV2
import github.gilbertokpl.base.internal.cache.*
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

class Cache(lf: BasePlugin) {
    private val lunarFrame = lf

    fun stop() {
        transaction(lunarFrame.sql) {
            for (i in toByteUpdate) {
                i.update()
            }
        }
    }

    fun simpleBoolean(): CacheBuilder<Boolean> {
        return SimpleCacheBuilder()
    }

    fun simpleInteger(): CacheBuilder<Int> {
        return SimpleCacheBuilder()
    }

    fun simplePlayer(): CacheBuilder<Player?> {
        return SimpleCacheBuilder()
    }

    fun string(cacheBase: CacheBase, column: Column<String>): CacheBuilder<String> {
        val instance = ByteCacheBuilder(cacheBase.table, cacheBase.primaryColumn, column)
        toByteUpdate.add(instance)
        return instance
    }

    fun boolean(cacheBase: CacheBase, column: Column<Boolean>): CacheBuilder<Boolean> {
        val instance = ByteCacheBuilder(cacheBase.table, cacheBase.primaryColumn, column)
        toByteUpdate.add(instance)
        return instance
    }

    fun integer(cacheBase: CacheBase, column: Column<Int>): CacheBuilder<Int> {
        val instance = ByteCacheBuilder(cacheBase.table, cacheBase.primaryColumn, column)
        toByteUpdate.add(instance)
        return instance
    }

    fun double(cacheBase: CacheBase, column: Column<Double>): CacheBuilder<Double> {
        val instance = ByteCacheBuilder(cacheBase.table, cacheBase.primaryColumn, column)
        toByteUpdate.add(instance)
        return instance
    }

    fun long(cacheBase: CacheBase, column: Column<Long>): CacheBuilder<Long> {
        val instance = ByteCacheBuilder(cacheBase.table, cacheBase.primaryColumn, column)
        toByteUpdate.add(instance)
        return instance
    }

    fun location(
        cacheBase: CacheBase,
        column: Column<String>,
        base: SerializatorBase<Location?, String>
    ): CacheBuilder<Location?> {
        val instance = LocationCacheBuilder(cacheBase.table, cacheBase.primaryColumn, column, base)
        toByteUpdate.add(instance)
        return instance
    }

    fun <V> stringList(
        cacheBase: CacheBase,
        column: Column<String>,
        base: SerializatorBase<ArrayList<V>, String>
    ): CacheBuilderV2<ArrayList<V>, V> {
        val instance = ListCacheBuilder(cacheBase.table, cacheBase.primaryColumn, column, base)
        toByteUpdate.add(instance)
        return instance
    }

    fun <V, K> stringHashMap(
        cacheBase: CacheBase,
        column: Column<String>,
        base: SerializatorBase<HashMap<V, K>, String>
    ): CacheBuilderV2<HashMap<V, K>, V> {
        val instance = HashMapCacheBuilder(cacheBase.table, cacheBase.primaryColumn, column, base)
        toByteUpdate.add(instance)
        return instance
    }

    fun <V, K> integerHashMap(
        cacheBase: CacheBase,
        column: Column<Int>,
        base: SerializatorBase<HashMap<V, K>, Int>
    ): CacheBuilderV2<HashMap<V, K>, V> {
        val instance = HashMapCacheBuilder(cacheBase.table, cacheBase.primaryColumn, column, base)
        toByteUpdate.add(instance)
        return instance
    }


    fun start(cachePackage: String) {
       lunarFrame.getReflection().getClasses(cachePackage)
        transaction(lunarFrame.sql) {
            for (i in toByteUpdate) {
                i.load()
            }
        }
    }

    private val toByteUpdate = ArrayList<CacheBuilder<*>>()

}