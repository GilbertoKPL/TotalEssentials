package github.gilbertokpl.core.cache

import github.gilbertokpl.core.TotalCore
import github.gilbertokpl.core.cache.builder.*
import github.gilbertokpl.core.cache.interfaces.ICacheSerializer
import github.gilbertokpl.core.cache.interfaces.ICache
import github.gilbertokpl.core.cache.interfaces.ICacheBuilder
import github.gilbertokpl.core.cache.interfaces.ICacheBuilderExtended
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CacheManager(private val totalCore: TotalCore) {

    // =========================================================
    // Internal state
    // =========================================================
    val toByteUpdate = mutableListOf<ICacheBuilder<*>>()

    // =========================================================
    // Lifecycle methods
    // =========================================================

    /**
     * Start cache system: load classes from package and schedule periodic save.
     */
    fun start(cachePackage: String) {
        totalCore.getReflection().getClasses(cachePackage)

        transaction(totalCore.sql) {
            toByteUpdate.forEach(ICacheBuilder<*>::load)
        }

        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
            { save() },
            5, 5, TimeUnit.MINUTES
        )
    }

    /**
     * Stop cache system: unload all builders.
     */
    fun stop() {
        transaction(totalCore.sql) {
            toByteUpdate.forEach(ICacheBuilder<*>::unload)
        }
    }

    /**
     * Save all cache data into the database.
     */
    fun save() {
        try {
            transaction(totalCore.sql) {
                toByteUpdate.forEach {
                    try {
                        it.update()
                    } catch (e: Exception) {
                        Bukkit.getServer().shutdown()
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            Bukkit.getServer().shutdown()
            e.printStackTrace()
        }
    }

    // =========================================================
    // Simple builders (in-memory only, no DB persistence)
    // =========================================================

    fun simpleBoolean(): ICacheBuilder<Boolean> = SimpleBuilder()
    fun simpleInteger(): ICacheBuilder<Int> = SimpleBuilder()
    fun simpleLong(): ICacheBuilder<Long> = SimpleBuilder()
    fun simplePlayer(): ICacheBuilder<Player?> = SimpleBuilder()
    fun <T> simpleList(): ICacheBuilder<List<T>> = SimpleBuilder()

    // =========================================================
    // Persistent builders (linked to database columns)
    // =========================================================

    fun string(cacheBase: ICache, column: Column<String>): ICacheBuilder<String> =
        createCacheBuilder(cacheBase, column)

    fun boolean(cacheBase: ICache, column: Column<Boolean>): ICacheBuilder<Boolean> =
        createCacheBuilder(cacheBase, column)

    fun integer(cacheBase: ICache, column: Column<Int>): ICacheBuilder<Int> =
        createCacheBuilder(cacheBase, column)

    fun double(cacheBase: ICache, column: Column<Double>): ICacheBuilder<Double> =
        createCacheBuilder(cacheBase, column)

    fun long(cacheBase: ICache, column: Column<Long>): ICacheBuilder<Long> =
        createCacheBuilder(cacheBase, column)

    fun location(
        cacheBase: ICache,
        column: Column<String>,
        base: ICacheSerializer<Location?, String>
    ): ICacheBuilder<Location?> {
        val instance = LocationBuilder(cacheBase.table, cacheBase.primaryColumn, column, base)
        toByteUpdate.add(instance)
        return instance
    }

    fun <V> list(
        cacheBase: ICache,
        column: Column<String>,
        base: ICacheSerializer<ArrayList<V>, String>
    ): ICacheBuilderExtended<ArrayList<V>, V> {
        val instance = ListBuilder(cacheBase.table, cacheBase.primaryColumn, column, base)
        toByteUpdate.add(instance)
        return instance
    }

    fun <V, K> hashMap(
        cacheBase: ICache,
        column: Column<String>,
        base: ICacheSerializer<HashMap<V, K>, String>
    ): ICacheBuilderExtended<HashMap<V, K>, V> {
        val instance = HashMapBuilder(cacheBase.table, cacheBase.primaryColumn, column, base)
        toByteUpdate.add(instance)
        return instance
    }

    fun <V, K> integerHashMap(
        cacheBase: ICache,
        column: Column<Int>,
        base: ICacheSerializer<HashMap<V, K>, Int>
    ): ICacheBuilderExtended<HashMap<V, K>, V> {
        val instance = HashMapBuilder(cacheBase.table, cacheBase.primaryColumn, column, base)
        toByteUpdate.add(instance)
        return instance
    }

    // =========================================================
    // Internal factory
    // =========================================================

    private fun <T> createCacheBuilder(
        cacheBase: ICache,
        column: Column<T>
    ): ByteBuilder<T> {
        val instance = ByteBuilder(cacheBase.table, cacheBase.primaryColumn, column)
        toByteUpdate.add(instance)
        return instance
    }
}

