package github.gilbertokpl.essentialsk.data

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import github.gilbertokpl.essentialsk.tables.KitsDataSQL
import github.gilbertokpl.essentialsk.util.*
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

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

    fun createNewKitData(s: CommandSender? = null) {
        //cache
        Dao.getInstance().kitsCache[nameLowerCase] = KitDataInternal(nameLowerCase, name, emptyList(), 0L)
        reloadGui()

        //sql
        TaskUtil.getInstance().asyncExecutor {
            try {
                transaction(SqlUtil.getInstance().sql) {
                    KitsDataSQL.insert {
                        it[kitName] = nameLowerCase
                        it[kitFakeName] = name
                    }
                }
                s?.sendMessage(
                    GeneralLang.getInstance().kitsCreateKitSuccess.replace(
                        "%kit%",
                        nameLowerCase
                    )
                )
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
        }
    }

    fun loadKitCache() {
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
    }

    fun kitList(): List<String> {
        val list = ArrayList<String>()
        for (i in Dao.getInstance().kitsCache) {
            list.add(i.key)
        }
        return list
    }

    fun delKitData(s: CommandSender? = null) {
        //cache
        Dao.getInstance().kitsCache.remove(nameLowerCase)

        ReflectUtil.getInstance().getPlayers().forEach {
            PlayerData(it.name).delKitTime(nameLowerCase)
        }
        reloadGui()

        //sql
        TaskUtil.getInstance().asyncExecutor {
            try {
                transaction(SqlUtil.getInstance().sql) {
                    KitsDataSQL.deleteWhere { KitsDataSQL.kitName eq nameLowerCase }
                }
                s?.sendMessage(GeneralLang.getInstance().kitsDelKitSuccess.replace("%kit%", name))
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
        }
    }

    fun setFakeName(fakeName: String, s: CommandSender? = null) {
        //cache
        getCache().fakeName = fakeName
        reloadGui()

        //sql
        kitHelper(
            KitsDataSQL.kitFakeName,
            fakeName,
            GeneralLang.getInstance().kitsEditKitSuccess.replace("%kit%", name),
            s
        )
    }

    fun setItems(items: List<ItemStack>, s: CommandSender? = null) {
        //cache
        getCache().items = items
        reloadGui()

        val toSave = ItemUtil.getInstance().itemSerializer(items)

        //sql
        kitHelper(
            KitsDataSQL.kitItems,
            toSave,
            GeneralLang.getInstance().kitsEditKitSuccess.replace("%kit%", name),
            s
        )
    }

    fun setTime(time: Long, s: CommandSender? = null) {
        //cache
        getCache().time = time
        reloadGui()

        //sql
        kitHelper(
            KitsDataSQL.kitTime,
            time,
            GeneralLang.getInstance().kitsEditKitSuccess.replace("%kit%", name),
            s
        )
    }

    private fun <T> kitHelper(col: Column<T>, value: T, message: String, s: CommandSender? = null) {
        TaskUtil.getInstance().asyncExecutor {
            try {
                transaction(SqlUtil.getInstance().sql) {
                    KitsDataSQL.update({ KitsDataSQL.kitName eq nameLowerCase }) {
                        it[col] = value
                    }
                }
                s?.sendMessage(message)
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
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
        KitGuiInventory.setup()
    }

    //data class
    data class KitDataInternal(val name: String, var fakeName: String, var items: List<ItemStack>, var time: Long)
}