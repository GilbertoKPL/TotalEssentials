package github.gilbertokpl.essentialsk.data

import com.github.benmanes.caffeine.cache.Caffeine
import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import github.gilbertokpl.essentialsk.tables.KitsDataSQL
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.tables.PlayerDataSQL.uuid
import github.gilbertokpl.essentialsk.util.ItemUtil
import github.gilbertokpl.essentialsk.util.ReflectUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.concurrent.CompletableFuture

class KitData(kitName: String) {
    private val name = kitName
    private val nameLowerCase = kitName.lowercase()

    fun checkCache(): Boolean {
        Dao.getInstance().kitsCache.getIfPresent(nameLowerCase).also {
            if (it == null) {
                return false
            }
            return true
        }
    }

    fun createNewKitData() {
        //cache
        Dao.getInstance().kitsCache.put(
            nameLowerCase,
            CompletableFuture.supplyAsync { KitDataInternal(nameLowerCase, name, emptyList(), 0L) })
        reloadGui()

        TaskUtil.getInstance().asyncExecutor {
            //sql
            transaction(SqlUtil.getInstance().sql) {
                KitsDataSQL.insert {
                    it[kitName] = nameLowerCase
                    it[kitFakeName] = name
                    it[kitItems] = ""
                    it[kitTime] = 0L
                }
            }
        }
    }

    fun delKitData() {
        //cache
        Dao.getInstance().kitsCache.synchronous().invalidate(nameLowerCase)
        reloadGui()

        TaskUtil.getInstance().asyncExecutor {
            //cache
            ReflectUtil.getInstance().getPlayers().forEach {
                PlayerData(it).delKitTime(nameLowerCase)
            }

            //sql
            transaction(SqlUtil.getInstance().sql) {
                KitsDataSQL.deleteWhere { KitsDataSQL.kitName eq nameLowerCase }

                for (i in PlayerDataSQL.selectAll()) {
                    var list = ""
                    for (values in i[PlayerDataSQL.kitsTime].split("-")) {
                        values.split(".").also {
                            if (it[0] != nameLowerCase) {
                                if (list == "") {
                                    list = values
                                } else {
                                    list += "$list-$values"
                                }
                            }
                        }
                    }
                    PlayerDataSQL.update({ uuid like i[uuid] }) {
                        it[kitsTime] = list
                    }
                }
            }
        }
    }

    fun setFakeName(fakeName: String) {
        //cache
        getCache().fakeName = fakeName
        reloadGui()

        TaskUtil.getInstance().asyncExecutor {
            //sql
            transaction(SqlUtil.getInstance().sql) {
                KitsDataSQL.update ({ KitsDataSQL.kitName eq nameLowerCase}) {
                    it[kitFakeName] = fakeName
                }
            }
        }
    }

    fun setItems(items: List<ItemStack>) {
        //cache
        getCache().items = items
        reloadGui()

        TaskUtil.getInstance().asyncExecutor {
            //sql
            transaction(SqlUtil.getInstance().sql) {
                KitsDataSQL.update ({ KitsDataSQL.kitName eq nameLowerCase}) {
                    it[kitItems] = ItemUtil.getInstance().itemSerializer(items)
                }
            }
        }
    }

    fun setTime(time: Long) {
        //cache
        getCache().time = time
        reloadGui()

        TaskUtil.getInstance().asyncExecutor {
            //sql
            transaction(SqlUtil.getInstance().sql) {
                KitsDataSQL.update ({ KitsDataSQL.kitName eq nameLowerCase}) {
                    it[kitTime] = time
                }
            }
        }
    }

    fun getCache(): KitDataInternal {
        Dao.getInstance().kitsCache.getIfPresent(nameLowerCase).also {
            if (it == null) {
                createNewKitData()
                return Dao.getInstance().kitsCache.getIfPresent(nameLowerCase)!!.get()
            }
            return it.get()
        }
    }

    private fun reloadGui() {
        KitGuiInventory.kitGuiInventory()
    }

    //data class
    data class KitDataInternal(val name: String, var fakeName: String, var items: List<ItemStack>, var time: Long)
}