package github.gilbertokpl.essentialsk.data.sql

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.objects.KitDataV2
import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import github.gilbertokpl.essentialsk.tables.KitsDataSQL
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.command.CommandSender
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object KitDataSQLUtil {

    private fun reloadGui() {
        KitGuiInventory.setup()
    }

    fun delKitData(s: CommandSender? = null, name: String) {
        //cache
        DataManager.kitCacheV2.remove(name.lowercase())

        reloadGui()

        //sql
        TaskUtil.asyncExecutor {
            try {
                transaction(SqlUtil.sql) {
                    KitsDataSQL.deleteWhere { KitsDataSQL.kitName eq name.lowercase() }
                }
                s?.sendMessage(GeneralLang.kitsDelKitSuccess.replace("%kit%", name))
            } catch (ex: Exception) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
            }
        }
    }

    fun createNewKitData(s: CommandSender? = null, name: String) {
        //cache
        DataManager.kitCacheV2[name.lowercase()] =
            KitDataV2(name.lowercase(), name, emptyList(), 0L, 0)
        reloadGui()

        //sql
        TaskUtil.asyncExecutor {
            try {
                transaction(SqlUtil.sql) {
                    KitsDataSQL.insert {
                        it[kitName] = name.lowercase()
                        it[kitFakeName] = name
                    }
                }
                s?.sendMessage(
                    GeneralLang.kitsCreateKitSuccess.replace(
                        "%kit%",
                        name.lowercase()
                    )
                )
            } catch (ex: Exception) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
            }
        }
    }

    fun <T> kitHelper(col: Column<T>, value: T, message: String, name: String, s: CommandSender? = null) {
        TaskUtil.asyncExecutor {
            try {
                transaction(SqlUtil.sql) {
                    KitsDataSQL.update({ KitsDataSQL.kitName eq name }) {
                        it[col] = value
                    }
                }
                s?.sendMessage(message)
            } catch (ex: Exception) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
            }
        }
    }
}
