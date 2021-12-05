package github.gilbertokpl.essentialsk.data

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.kitsTime
import github.gilbertokpl.essentialsk.tables.KitsDataSQL
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.FakeNick
import github.gilbertokpl.essentialsk.util.PluginUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

class PlayerData(player: Player) {
    private val uuid = PluginUtil.getInstance().getPlayerUUID(player)
    private val p = player

    fun unloadCache() {
        Dao.getInstance().playerCache.synchronous().invalidate(uuid)
    }

    fun loadCache() {
        TaskUtil.getInstance().asyncExecutor {
            transaction(SqlUtil.getInstance().sql) {
                // kits
                val query = PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }
                val cache = Caffeine.newBuilder().maximumSize(500).build<String, Long>()
                if (query.empty()) {
                    getCache().kitsCache = cache
                    PlayerDataSQL.insert {
                        it[uuid] = this@PlayerData.uuid
                        it[kitsTime] = ""
                        it[FakeNick] = ""
                    }
                    return@transaction
                }
                var toPut = ""
                for (kits in query.single()[kitsTime].split("-")) {
                    if (kits == "") continue
                    val split = kits.split(".")
                    val timeKit = split[1].toLong()
                    val nameKit = split[0]

                    val timeAll = KitsDataSQL.select { KitsDataSQL.kitName eq nameKit }.single()[KitsDataSQL.kitTime] + timeKit

                    if (timeKit != 0L || timeAll > System.currentTimeMillis()) {
                        if (toPut == "") {
                            toPut = kits
                        } else {
                            toPut += "-$kits"
                        }
                        cache.put(nameKit, timeKit)
                    }

                }
                getCache().kitsCache = cache

                PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                    it[kitsTime] = toPut
                }

                //nick
                val newNick = query.single()[FakeNick]
                if (newNick != "") {
                    getCache().FakeNick = query.single()[FakeNick]
                    p.setDisplayName(newNick)
                }
            }
        }
    }

    fun delKitTime(kit: String) {
        val cache = getCache().kitsCache
        cache.invalidate(kit)
        getCache().kitsCache = cache
    }

    fun setKitTime(kit: String, time: Long) {
        //cache
        val cache = getCache().kitsCache
        cache.invalidate(kit)
        cache.put(kit, time)
        getCache().kitsCache = cache

        //sql
        TaskUtil.getInstance().asyncExecutor {
            transaction(SqlUtil.getInstance().sql) {
                val work = PlayerDataSQL.select { PlayerDataSQL.uuid eq uuid }
                if (work.empty() || work.single()[kitsTime] == "") {
                    PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                        it[kitsTime] = "$kit.${System.currentTimeMillis()}"
                    }
                    return@transaction
                }
                val check = work.single()[kitsTime].split("-")
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
        PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
            it[kitsTime] = newNick
        }
    }

    fun setNick(newNick: String) : CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        TaskUtil.getInstance().asyncExecutor {
            transaction(SqlUtil.getInstance().sql) {
                if (MainConfig.getInstance().nicksCanPlayerHaveSameNick) {
                    for (i in PlayerDataSQL.selectAll()) {
                        if (i[FakeNick].equals(newNick, true)) {
                            future.complete(true)
                            break
                        }
                    }
                }
                val nick = PluginUtil.getInstance().colorPermission(p, newNick)
                getCache().FakeNick = nick
                PlayerDataSQL.update({ PlayerDataSQL.uuid eq uuid }) {
                    it[kitsTime] = nick
                }
                future.complete(false)
            }
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
        Dao.getInstance().playerCache.getIfPresent(uuid).also {
            return if (it != null) {
                it.get()
            } else {
                Dao.getInstance().playerCache.put(
                    uuid,
                    CompletableFuture.supplyAsync {
                        InternalPlayerData(
                            uuid,
                            Caffeine.newBuilder().maximumSize(500).build(),
                            ""
                        )
                    })
                Dao.getInstance().playerCache.getIfPresent(uuid)!!.get()
            }
        }
    }

    data class InternalPlayerData(val playerUUID: String, var kitsCache: Cache<String, Long>, var FakeNick: String)
}