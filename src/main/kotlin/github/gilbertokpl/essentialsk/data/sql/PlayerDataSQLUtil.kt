package github.gilbertokpl.essentialsk.data.sql

import github.gilbertokpl.essentialsk.manager.IInstance
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.util.LocationUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import org.bukkit.Location
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class PlayerDataSQLUtil {

    fun getHomeLocationSQL(home: String, playerID: String): Location? {
        lateinit var homesList: String
        transaction(SqlUtil.getInstance().sql) {
            PlayerDataSQL.select { PlayerDataSQL.PlayerInfo eq playerID }.also { query ->
                homesList = query.single()[PlayerDataSQL.SavedHomes]
            }
        }
        for (h in homesList.split("|")) {
            if (h == "") continue
            val split = h.split(",")
            if (home.lowercase() == split[0]) {
                return LocationUtil.getInstance().locationSerializer(split[1])
            }
        }
        return null
    }

    fun getHomeListSQL(playerID: String): List<String> {
        lateinit var homesList: String
        val cacheHomes = ArrayList<String>()
        var bol = false
        transaction(SqlUtil.getInstance().sql) {
            PlayerDataSQL.select { PlayerDataSQL.PlayerInfo eq playerID }.also { query ->
                if (query.empty()) {
                    bol = true
                    return@transaction
                }
                homesList = query.single()[PlayerDataSQL.SavedHomes]
            }
        }
        if (bol) {
            return emptyList()
        }
        for (h in homesList.split("|")) {
            if (h == "") continue
            val split = h.split(",")
            val nameHome = split[0]
            cacheHomes.add(nameHome)
        }
        return cacheHomes
    }

    fun delHomeSQL(name: String, playerID: String) {

        lateinit var homes: String

        transaction(SqlUtil.getInstance().sql) {
            PlayerDataSQL.select { PlayerDataSQL.PlayerInfo eq playerID }.also { query ->
                homes = query.single()[PlayerDataSQL.SavedHomes]
            }
        }

        var newHome = ""

        for (h in homes.split("|")) {
            if (h.split(",")[0] == name) continue
            newHome += if (newHome == "") {
                h
            } else {
                "|$h"
            }
        }

        transaction(SqlUtil.getInstance().sql) {
            PlayerDataSQL.update({ PlayerDataSQL.PlayerInfo eq playerID }) {
                it[SavedHomes] = newHome
            }
        }

    }

    fun setHomeSQL(name: String, loc: Location, playerID: String) {
        lateinit var homes: String
        val serializedHome = LocationUtil.getInstance().locationSerializer(loc)
        var emptyQuery = false
        transaction(SqlUtil.getInstance().sql) {
            PlayerDataSQL.select { PlayerDataSQL.PlayerInfo eq playerID }.also { query ->
                emptyQuery = query.empty()
                if (emptyQuery) {
                    PlayerDataSQL.update({ PlayerDataSQL.PlayerInfo eq playerID }) {
                        it[SavedHomes] = "$name,$serializedHome"
                    }
                    return@transaction
                }
                homes = query.single()[PlayerDataSQL.SavedHomes]
            }
        }

        if (emptyQuery) return

        var newHome = "$name,$serializedHome"

        for (h in homes.split("|")) {
            if (h == "") continue
            newHome += "|$h"
        }

        transaction(SqlUtil.getInstance().sql) {
            PlayerDataSQL.update({ PlayerDataSQL.PlayerInfo eq playerID }) {
                it[SavedHomes] = newHome
            }
        }
    }

    fun setKitTimeSQL(kit: String, time: Long, playerID: String) {
        var query = false
        lateinit var kitTime: String

        transaction(SqlUtil.getInstance().sql) {
            PlayerDataSQL.select { PlayerDataSQL.PlayerInfo eq playerID }.also {
                query = it.empty()
                kitTime = it.single()[PlayerDataSQL.KitsTime]
            }
        }

        if (query || kitTime == "") {
            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.update({ PlayerDataSQL.PlayerInfo eq playerID }) {
                    it[KitsTime] = "$kit,$time"
                }
            }
            return
        }
        var newPlace = ""
        for (i in kitTime.split("|")) {
            if (i.split(",")[0] != kit) {
                newPlace += if (newPlace == "") {
                    i
                } else {
                    "|$i"
                }
            }
        }
        newPlace += if (newPlace == "") {
            "$kit,$time"
        } else {
            "|$kit,$time"
        }
        transaction(SqlUtil.getInstance().sql) {
            PlayerDataSQL.update({ PlayerDataSQL.PlayerInfo eq playerID }) {
                it[KitsTime] = newPlace
            }
        }
    }

    companion object : IInstance<PlayerDataSQLUtil> {
        private val instance = createInstance()
        override fun createInstance(): PlayerDataSQLUtil = PlayerDataSQLUtil()
        override fun getInstance(): PlayerDataSQLUtil {
            return instance
        }
    }
}