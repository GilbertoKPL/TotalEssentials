package github.gilbertokpl.essentialsk.data

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.FakeNick
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.kitsTime
import github.gilbertokpl.essentialsk.util.PluginUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.concurrent.CompletableFuture


class PlayerData(player: Player) {

    private val uuid = PluginUtil.getInstance().getPlayerUUID(player)
    private val p = player

    fun unloadCache() {
        Dao.getInstance().playerCache.remove(uuid)
    }

    fun loadCache() {
        TaskUtil.getInstance().asyncExecutor {
            val cache = HashMap<String, Long>(40)
            lateinit var timeKits: String
            lateinit var fakeNick: String
            var emptyQuery = false

            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }.also { query ->
                    emptyQuery = query.empty()
                    if (emptyQuery) {
                        PlayerDataSQL.insert {
                            it[uuid] = this@PlayerData.uuid
                            it[kitsTime] = ""
                            it[FakeNick] = ""
                        }
                        return@transaction
                    }
                    timeKits = query.single()[kitsTime]
                    fakeNick = query.single()[FakeNick]
                }
            }

            if (emptyQuery) {
                getCache().kitsCache = cache
                return@asyncExecutor
            }

            for (kits in timeKits.split("-")) {
                if (kits == "") continue
                val split = kits.split(".")
                val timeKit = split[1].toLong()
                val nameKit = split[0]

                val timeAll = KitData(nameKit).getCache(true)

                if (timeKit != 0L && timeAll != null && (timeAll.time + timeKit) > System.currentTimeMillis()) {
                    cache[nameKit] = timeKit
                    continue
                }

            }

            getCache().kitsCache = cache

            //nick
            if (fakeNick != "") {
                getCache().FakeNick = fakeNick
                p.setDisplayName(fakeNick)
            }
        }
    }

    fun delKitTime(kit: String) {
        val cache = getCache().kitsCache
        cache.remove(kit)
        getCache().kitsCache = cache
    }

    fun setKitTime(kit: String, time: Long) {
        //cache
        val cache = getCache().kitsCache
        cache.remove(kit)
        cache[kit] = time
        getCache().kitsCache = cache

        //sql
        TaskUtil.getInstance().asyncExecutor {

            var query = false
            lateinit var kitTime: String

            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }.also {
                    query = it.empty()
                    kitTime = it.single()[kitsTime]
                }
            }

            if (query || kitTime == "") {
                transaction(SqlUtil.getInstance().sql) {
                    PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                        it[kitsTime] = "$kit.$time"
                    }
                }
                return@asyncExecutor
            }
            val check = kitTime.split("-")
            var newPlace = ""
            for (i in check) {
                if (i.split(".")[0] != kit) {
                    if (newPlace == "") {
                        newPlace += i
                        continue
                    }
                    newPlace += "-$i"
                    continue
                }
            }
            newPlace += if (newPlace == "") {
                "$kit.${System.currentTimeMillis()}"
            } else {
                "-$kit.$time"
            }
            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                    it[kitsTime] = newPlace
                }
            }
        }
    }

    fun setNick(newNick: String, color: Boolean) {
        if (color) {
            newNick.replace("&", "§")
        }
        getCache().FakeNick = newNick
        TaskUtil.getInstance().asyncExecutor {
            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                    it[kitsTime] = newNick
                }
            }
        }
    }

    fun setNick(newNick: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        lateinit var players: List<String>
        transaction(SqlUtil.getInstance().sql) {
            players = PlayerDataSQL.selectAll().map { it[FakeNick] }
        }
        if (MainConfig.getInstance().nicksCanPlayerHaveSameNick) {
            for (i in players) {
                if (i.equals(newNick, true)) {
                    future.complete(true)
                    break
                }
            }
        }
        val nick = PluginUtil.getInstance().colorPermission(p, newNick)
        getCache().FakeNick = nick
        transaction(SqlUtil.getInstance().sql) {
            PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                it[kitsTime] = nick
            }
            future.complete(false)
        }
        return future
    }

    fun removeNick() {
        //cache
        getCache().FakeNick = ""

        //sql
        TaskUtil.getInstance().asyncExecutor {
            transaction(SqlUtil.getInstance().sql) {
                PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                    it[kitsTime] = ""
                }
            }
        }
    }


    fun getCache(): InternalPlayerData {
        Dao.getInstance().playerCache[uuid].also {
            return if (it != null) {
                it
            } else {
                Dao.getInstance().playerCache[uuid] = InternalPlayerData(
                    uuid,
                    HashMap(40),
                    ""
                )
                Dao.getInstance().playerCache[uuid]!!
            }
        }
    }

    data class InternalPlayerData(val playerUUID: String, var kitsCache: HashMap<String, Long>, var FakeNick: String)
}