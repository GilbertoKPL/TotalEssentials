package github.genesyspl.cardinal.data.`object`

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.data.sql.PlayerDataSQLUtil
import github.genesyspl.cardinal.tables.PlayerDataSQL
import github.genesyspl.cardinal.util.FileLoggerUtil
import github.genesyspl.cardinal.util.PlayerUtil
import github.genesyspl.cardinal.util.SqlUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Location
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class OfflinePlayerData(player: String) {
    private val p = github.genesyspl.cardinal.Cardinal.instance.server.getPlayerExact(player)

    private val online = p != null

    private val playerID = if (p != null) {
        PlayerUtil.getInstance().getPlayerUUID(p)
    } else {
        @Suppress("DEPRECATION")
        PlayerUtil.getInstance()
            .getPlayerUUID(github.genesyspl.cardinal.Cardinal.instance.server.getOfflinePlayer(player))
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
        } else {
            PlayerDataSQLUtil.getInstance().getHomeLocationSQL(home, playerID)
        }
    }

    fun getHomeList(): List<String> {
        return if (online) {
            data!!.getHomeList()
        } else {
            PlayerDataSQLUtil.getInstance().getHomeListSQL(playerID)
        }
    }

    fun delHome(name: String) {
        if (online) {
            data!!.delHome(name)
        } else {
            PlayerDataSQLUtil.getInstance().delHomeSQL(name, playerID)
        }
    }

    fun setHome(name: String, loc: Location) {
        if (online) {
            data!!.setHome(name, loc)
        } else {
            PlayerDataSQLUtil.getInstance().setHomeSQL(name, loc, playerID)
        }
    }
}