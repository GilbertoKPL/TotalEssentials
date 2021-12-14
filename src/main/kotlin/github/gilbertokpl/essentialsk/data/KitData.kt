package github.gilbertokpl.essentialsk.data

import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import github.gilbertokpl.essentialsk.tables.KitsDataSQL
import github.gilbertokpl.essentialsk.util.*
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

class KitData(kitName: String) {

    private val name = kitName
    private val nameLowerCase = kitName.lowercase()

    fun checkCache(): Boolean {
        Dao.getInstance().kitsCache[nameLowerCase].also {
            if (it == null) {
                return true
            }
            return true
        }
    }

    fun createNewKitData(): Boolean {
        //cache
        Dao.getInstance().kitsCache[nameLowerCase] = KitDataInternal(nameLowerCase, name, emptyList(), 0L)
        reloadGui()

        //sql
        return CompletableFuture.supplyAsync({
            try {
                transaction(SqlUtil.getInstance().sql) {
                    KitsDataSQL.insert {
                        it[kitName] = nameLowerCase
                        it[kitFakeName] = name
                    }
                }
                return@supplyAsync true
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
                return@supplyAsync false
            }
        }, TaskUtil.getInstance().getExecutor()).get()
    }

    fun loadKitCache(): Boolean {
        return CompletableFuture.supplyAsync({
            try {
                Dao.getInstance().kitsCache.clear()
                transaction(SqlUtil.getInstance().sql) {
                    for (values in KitsDataSQL.selectAll()) {
                        val kit = values[KitsDataSQL.kitName]
                        val kitFakeName = values[KitsDataSQL.kitFakeName]
                        val kitTime = values[KitsDataSQL.kitTime]
                        val item = values[KitsDataSQL.kitItems]
                        Dao.getInstance().kitsCache[kit] = KitDataInternal(
                            kit,
                            kitFakeName,
                            ItemUtil.getInstance().itemSerializer(item),
                            kitTime
                        )
                    }
                }
                return@supplyAsync true
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
            return@supplyAsync false
        }, TaskUtil.getInstance().getExecutor()).get()
    }

    fun kitList(): List<String> {
        val list = ArrayList<String>()
        for (i in Dao.getInstance().kitsCache) {
            list.add(i.key)
        }
        return list
    }

    fun delKitData(): Boolean {
        //cache
        Dao.getInstance().kitsCache.remove(nameLowerCase)

        ReflectUtil.getInstance().getPlayers().forEach {
            PlayerData(it.name).delKitTime(nameLowerCase)
        }
        reloadGui()

        //sql
        return CompletableFuture.supplyAsync({
            try {
                transaction(SqlUtil.getInstance().sql) {
                    KitsDataSQL.deleteWhere { KitsDataSQL.kitName eq nameLowerCase }
                }
                return@supplyAsync true
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
                return@supplyAsync false
            }
        }, TaskUtil.getInstance().getExecutor()).get()
    }

    fun setFakeName(fakeName: String): Boolean {
        //cache
        getCache().fakeName = fakeName
        reloadGui()

        //sql
        return kitHelper(KitsDataSQL.kitFakeName, fakeName).get()
    }

    fun setItems(items: List<ItemStack>): Boolean {
        //cache
        getCache().items = items
        reloadGui()

        val toSave = ItemUtil.getInstance().itemSerializer(items)

        //sql
        return kitHelper(KitsDataSQL.kitItems, toSave).get()
    }

    fun setTime(time: Long): Boolean {
        //cache
        getCache().time = time
        reloadGui()

        //sql
        return kitHelper(KitsDataSQL.kitTime, time).get()
    }

    private fun <T> kitHelper(col: Column<T>, value: T): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync({
            try {
                transaction(SqlUtil.getInstance().sql) {
                    KitsDataSQL.update({ KitsDataSQL.kitName eq nameLowerCase }) {
                        it[col] = value
                    }
                }
                return@supplyAsync true
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
                return@supplyAsync false
            }
        }, TaskUtil.getInstance().getExecutor())
    }

    fun getCache(): KitDataInternal {
        Dao.getInstance().kitsCache[nameLowerCase].also {
            if (it == null) {
                createNewKitData()
                return Dao.getInstance().kitsCache[nameLowerCase]!!
            }
            return it
        }
    }

    fun getCache(boolean: Boolean): KitDataInternal? {
        if (boolean) {
            return Dao.getInstance().kitsCache[nameLowerCase]
        }
        return getCache()
    }

    private fun reloadGui() {
        KitGuiInventory.setup()
    }

    //data class
    data class KitDataInternal(val name: String, var fakeName: String, var items: List<ItemStack>, var time: Long)
}