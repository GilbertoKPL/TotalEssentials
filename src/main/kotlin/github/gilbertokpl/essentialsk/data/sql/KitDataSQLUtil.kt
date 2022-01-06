package github.gilbertokpl.essentialsk.data.sql

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.`object`.KitDataV2
import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import github.gilbertokpl.essentialsk.manager.IInstance
import github.gilbertokpl.essentialsk.tables.KitsDataSQL
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.ReflectUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.command.CommandSender
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class KitDataSQLUtil {

    private fun reloadGui() {
        KitGuiInventory.setup()
    }

    fun delKitData(s: CommandSender? = null, name: String) {
        //cache
        DataManager.getInstance().kitCacheV2.remove(name.lowercase())

        ReflectUtil.getInstance().getPlayers().forEach {
            DataManager.getInstance().playerCacheV2[it.name.lowercase()]!!.delKitTime(name.lowercase())
        }
        reloadGui()

        //sql
        TaskUtil.getInstance().asyncExecutor {
            try {
                transaction(SqlUtil.getInstance().sql) {
                    KitsDataSQL.deleteWhere { KitsDataSQL.kitName eq name.lowercase() }
                }
                s?.sendMessage(GeneralLang.getInstance().kitsDelKitSuccess.replace("%kit%", name))
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
        }
    }

    fun createNewKitData(s: CommandSender? = null, name: String) {
        //cache
        DataManager.getInstance().kitCacheV2[name.lowercase()] =
            KitDataV2(name.lowercase(), name, emptyList(), 0L, 0)
        reloadGui()

        //sql
        TaskUtil.getInstance().asyncExecutor {
            try {
                transaction(SqlUtil.getInstance().sql) {
                    KitsDataSQL.insert {
                        it[kitName] = name.lowercase()
                        it[kitFakeName] = name
                    }
                }
                s?.sendMessage(
                    GeneralLang.getInstance().kitsCreateKitSuccess.replace(
                        "%kit%",
                        name.lowercase()
                    )
                )
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
        }
    }

    fun <T> kitHelper(col: Column<T>, value: T, message: String, name: String, s: CommandSender? = null) {
        TaskUtil.getInstance().asyncExecutor {
            try {
                transaction(SqlUtil.getInstance().sql) {
                    KitsDataSQL.update({ KitsDataSQL.kitName eq name }) {
                        it[col] = value
                    }
                }
                s?.sendMessage(message)
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
        }
    }

    companion object : IInstance<KitDataSQLUtil> {
        private val instance = createInstance()
        override fun createInstance(): KitDataSQLUtil = KitDataSQLUtil()
        override fun getInstance(): KitDataSQLUtil {
            return instance
        }
    }
}