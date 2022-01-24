package github.gilbertokpl.essentialsk.data.dao

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.data.util.PlayerDataSQLUtil
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.PlayerUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Location
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

internal class OfflinePlayerDAO(player: String) {
    private val p = EssentialsK.instance.server.getPlayerExact(player)

    private val online = p != null

    private val playerID = if (p != null) {
        PlayerUtil.getPlayerUUID(p)
    } else {
        @Suppress("DEPRECATION")
        PlayerUtil.getPlayerUUID(EssentialsK.instance.server.getOfflinePlayer(player))
    }

    val data = if (online) {
        PlayerDataDAO[p!!]
    } else {
        null
    }

    fun checkSql(): Boolean {
        return if (online) {
            true
        } else {
            try {
                var check = false
                transaction(SqlUtil.sql) {
                    check = PlayerDataSQL.select { PlayerDataSQL.playerTable eq playerID }.empty()
                }
                return !check
            } catch (ex: Exception) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
            }
            return false
        }
    }

    fun getHomeLocation(home: String): Location? {
        return if (online) {
            data!!.getHomeLocation(home)
        } else {
            PlayerDataSQLUtil.getHomeLocationSQL(home, playerID)
        }
    }

    fun getHomeList(): List<String> {
        return if (online) {
            data!!.getHomeList()
        } else {
            PlayerDataSQLUtil.getHomeListSQL(playerID)
        }
    }

    fun delHome(name: String) {
        if (online) {
            data!!.delHome(name)
        } else {
            PlayerDataSQLUtil.delHomeSQL(name, playerID)
        }
    }

    fun setHome(name: String, loc: Location) {
        if (online) {
            data!!.setHome(name, loc)
        } else {
            PlayerDataSQLUtil.setHomeSQL(name, loc, playerID)
        }
    }
}
