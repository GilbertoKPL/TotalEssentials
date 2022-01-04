package github.gilbertokpl.essentialsk.data.`object`

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.sql.PlayerDataSQLUtil
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.PlayerUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Location
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class OfflinePlayerData(player: String) {
    private val p = EssentialsK.instance.server.getPlayerExact(player)

    private val online = p != null

    private val playerID = if (p != null) {
        PlayerUtil.getInstance().getPlayerUUID(p)
    } else {
        @Suppress("DEPRECATION")
        PlayerUtil.getInstance().getPlayerUUID(EssentialsK.instance.server.getOfflinePlayer(player))
    }

    val data = if (online) {
        DataManager.getInstance().playerCacheV2[p!!.name.lowercase()]
    } else {
        null
    }

    fun checkSql(): Boolean {
        return if (online) {
            true
        } else {
            try {
                var check = false
                transaction(SqlUtil.getInstance().sql) {
                    check = PlayerDataSQL.select { PlayerDataSQL.PlayerInfo eq playerID }.empty()
                }
                return !check
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
            return false
        }
    }

    fun getHomeLocation(home: String): Location? {
        return if (online) {
            data!!.getHomeLocation(home)
        }
        else {
            PlayerDataSQLUtil.getInstance().getHomeLocationSQL(home, playerID)
        }
    }

    fun getHomeList(): List<String> {
        return if (online) {
            data!!.getHomeList()
        }
        else {
            PlayerDataSQLUtil.getInstance().getHomeListSQL(playerID)
        }
    }

    fun delHome(name: String) {
        if (online) {
            data!!.delHome(name)
        }
        else {
            PlayerDataSQLUtil.getInstance().delHomeSQL(name, playerID)
        }
    }

    fun setHome(name: String, loc: Location) {
        if (online) {
            data!!.setHome(name, loc)
        }
        else {
            PlayerDataSQLUtil.getInstance().setHomeSQL(name, loc, playerID)
        }
    }
}