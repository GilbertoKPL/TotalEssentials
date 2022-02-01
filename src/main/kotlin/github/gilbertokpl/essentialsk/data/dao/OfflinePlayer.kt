package github.gilbertokpl.essentialsk.data.dao

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.PlayerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.Location
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

internal class OfflinePlayer(player: String) {
    private val p = EssentialsK.instance.server.getPlayerExact(player)

    private val online = p != null

    private val playerID = if (p != null) {
        PlayerUtil.getPlayerUUID(p)
    } else {
        @Suppress("DEPRECATION")
        PlayerUtil.getPlayerUUID(EssentialsK.instance.server.getOfflinePlayer(player))
    }

    val data = if (online) {
        PlayerData[p!!]
    } else {
        null
    }

    fun checkSql(): Boolean {
        return if (online) {
            true
        } else {
            try {
                var check = false
                transaction(DataManager.sql) {
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
            null
        }
    }

    fun getHomeList(): List<String> {
        return if (online) {
            data!!.getHomeList()
        } else {
            emptyList()
        }
    }

    fun delHome(name: String) {
        if (online) {
            data!!.delHome(name)
        } else {
            //
        }
    }

    fun setHome(name: String, loc: Location) {
        if (online) {
            data!!.setHome(name, loc)
        } else {
            //
        }
    }
}
