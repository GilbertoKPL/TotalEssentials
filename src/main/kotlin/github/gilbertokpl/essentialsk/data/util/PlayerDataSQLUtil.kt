package github.gilbertokpl.essentialsk.data.util

import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.util.LocationUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import org.bukkit.Location
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object PlayerDataSQLUtil {

    fun getHomeLocationSQL(home: String, playerID: String): Location? {
        lateinit var homesList: String
        transaction(SqlUtil.sql) {
            PlayerDataSQL.select { PlayerDataSQL.playerTable eq playerID }.also { query ->
                homesList = query.single()[PlayerDataSQL.homeTable]
            }
        }
        for (h in homesList.split("|")) {
            if (h == "") continue
            val split = h.split(",")
            if (home.lowercase() == split[0]) {
                return LocationUtil.locationSerializer(split[1])
            }
        }
        return null
    }

    fun getHomeListSQL(playerID: String): List<String> {
        lateinit var homesList: String
        val cacheHomes = ArrayList<String>()
        var bol = false
        transaction(SqlUtil.sql) {
            PlayerDataSQL.select { PlayerDataSQL.playerTable eq playerID }.also { query ->
                if (query.empty()) {
                    bol = true
                    return@transaction
                }
                homesList = query.single()[PlayerDataSQL.homeTable]
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

        transaction(SqlUtil.sql) {
            PlayerDataSQL.select { PlayerDataSQL.playerTable eq playerID }.also { query ->
                homes = query.single()[PlayerDataSQL.homeTable]
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

        transaction(SqlUtil.sql) {
            PlayerDataSQL.update({ PlayerDataSQL.playerTable eq playerID }) {
                it[homeTable] = newHome
            }
        }

    }

    fun setHomeSQL(name: String, loc: Location, playerID: String) {
        lateinit var homes: String
        val serializedHome = LocationUtil.locationSerializer(loc)
        var emptyQuery = false
        transaction(SqlUtil.sql) {
            PlayerDataSQL.select { PlayerDataSQL.playerTable eq playerID }.also { query ->
                emptyQuery = query.empty()
                if (emptyQuery) {
                    PlayerDataSQL.update({ PlayerDataSQL.playerTable eq playerID }) {
                        it[homeTable] = "$name,$serializedHome"
                    }
                    return@transaction
                }
                homes = query.single()[PlayerDataSQL.homeTable]
            }
        }

        if (emptyQuery) return

        var newHome = "$name,$serializedHome"

        for (h in homes.split("|")) {
            if (h == "") continue
            newHome += "|$h"
        }

        transaction(SqlUtil.sql) {
            PlayerDataSQL.update({ PlayerDataSQL.playerTable eq playerID }) {
                it[homeTable] = newHome
            }
        }
    }

    fun setKitTimeSQL(kit: String, time: Long, playerID: String) {
        var query = false
        lateinit var kitTime: String

        transaction(SqlUtil.sql) {
            PlayerDataSQL.select { PlayerDataSQL.playerTable eq playerID }.also {
                query = it.empty()
                kitTime = it.single()[PlayerDataSQL.kitsTable]
            }
        }

        if (query || kitTime == "") {
            transaction(SqlUtil.sql) {
                PlayerDataSQL.update({ PlayerDataSQL.playerTable eq playerID }) {
                    it[kitsTable] = "$kit,$time"
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
        transaction(SqlUtil.sql) {
            PlayerDataSQL.update({ PlayerDataSQL.playerTable eq playerID }) {
                it[kitsTable] = newPlace
            }
        }
    }
}
