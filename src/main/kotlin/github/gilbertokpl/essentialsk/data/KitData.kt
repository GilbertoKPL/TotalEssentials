package github.gilbertokpl.essentialsk.data

import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import github.gilbertokpl.essentialsk.tables.KitsDataSQL
import github.gilbertokpl.essentialsk.util.ItemUtil
import github.gilbertokpl.essentialsk.util.ReflectUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.concurrent.CompletableFuture

class KitData(kitName: String) {

    private val name = kitName
    private val nameLowerCase = kitName.lowercase()

    fun checkCache(): Boolean {
        Dao.getInstance().kitsCache[nameLowerCase].also {
            if (it == null) {
                return false
            }
            return true
        }
    }

    fun createNewKitData() {
        //cache
        Dao.getInstance().kitsCache[nameLowerCase] = KitDataInternal(nameLowerCase, name, emptyList(), 0L)
        reloadGui()

        TaskUtil.getInstance().asyncExecutor {
            //sql
            transaction(SqlUtil.getInstance().sql) {
                KitsDataSQL.insert {
                    it[kitName] = nameLowerCase
                    it[kitFakeName] = name
                }
            }
        }
    }

    fun loadKitCache(): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
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
            future.complete(true)
        }
        return future
    }

    fun kitList(): List<String> {
        val list = ArrayList<String>()
        for (i in Dao.getInstance().kitsCache) {
            list.add(i.key)
        }
        return list
    }

    fun delKitData() {
        //cache
        Dao.getInstance().kitsCache.remove(nameLowerCase)

        ReflectUtil.getInstance().getPlayers().forEach {
            PlayerData(it.name).delKitTime(nameLowerCase)
        }
        reloadGui()

        TaskUtil.getInstance().asyncExecutor {
            //sql
            transaction(SqlUtil.getInstance().sql) {
                KitsDataSQL.deleteWhere { KitsDataSQL.kitName eq nameLowerCase }
            }
        }
    }

    fun setFakeName(fakeName: String) {
        //cache
        getCache().fakeName = fakeName
        reloadGui()

        TaskUtil.getInstance().asyncExecutor {
            //sql
            transaction(SqlUtil.getInstance().sql) {
                KitsDataSQL.update({ KitsDataSQL.kitName eq nameLowerCase }) {
                    it[kitFakeName] = fakeName
                }
            }
        }
    }

    fun setItems(items: List<ItemStack>) {
        //cache
        getCache().items = items
        reloadGui()

        val toSave = ItemUtil.getInstance().itemSerializer(items)

        TaskUtil.getInstance().asyncExecutor {
            //sql
            transaction(SqlUtil.getInstance().sql) {
                KitsDataSQL.update({ KitsDataSQL.kitName eq nameLowerCase }) {
                    it[kitItems] = toSave
                }
            }
        }
    }

    fun setTime(time: Long) {
        //cache
        getCache().time = time
        reloadGui()

        TaskUtil.getInstance().asyncExecutor {
            //sql
            transaction(SqlUtil.getInstance().sql) {
                KitsDataSQL.update({ KitsDataSQL.kitName eq nameLowerCase }) {
                    it[kitTime] = time
                }
            }
        }
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
        KitGuiInventory.kitGuiInventory()
    }

    //data class
    data class KitDataInternal(val name: String, var fakeName: String, var items: List<ItemStack>, var time: Long)
}