package github.gilbertokpl.essentialsk.data.dao

import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.DataManager.del
import github.gilbertokpl.essentialsk.data.DataManager.put
import github.gilbertokpl.essentialsk.data.tables.KitsDataSQL
import github.gilbertokpl.essentialsk.data.util.Serializator
import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

internal data class KitData(
    val kitNameCache: String,
    var fakeNameCache: String,
    var itemsCache: List<ItemStack>,
    var timeCache: Long,
    var weightCache: Int
) {

    fun setWeight(w: Int) {
        weightCache = w
        reloadGui()

        KitsDataSQL.put(kitNameCache, hashMapOf(KitsDataSQL.kitWeight to w))
    }

    fun setFakeName(newName: String) {
        fakeNameCache = newName
        reloadGui()

        KitsDataSQL.put(kitNameCache, hashMapOf(KitsDataSQL.kitFakeName to newName))
    }

    fun setItems(i: List<ItemStack>) {
        //cache
        itemsCache = i
        reloadGui()

        val toSave = Serializator.itemSerializer(i)

        KitsDataSQL.put(kitNameCache, hashMapOf(KitsDataSQL.kitItems to toSave))
    }

    fun setTime(t: Long) {
        //cache
        timeCache = t
        reloadGui()

        KitsDataSQL.put(kitNameCache, hashMapOf(KitsDataSQL.kitTime to t))
    }

    private fun reloadGui() {
        KitGuiInventory.setup()
    }

    companion object {

        private val kitCacheV2 = mutableMapOf<String, KitData>()

        operator fun get(warp: String) = kitCacheV2[warp.lowercase()]

        fun remove(warp: String) = kitCacheV2.remove(warp.lowercase())

        fun put(warp: String, data: KitData) = kitCacheV2.put(warp.lowercase(), data)

        fun getList() = kitCacheV2.map { it.key }

        fun getMap() = kitCacheV2

        private fun reloadGui() {
            KitGuiInventory.setup()
        }

        fun delKitData(name: String) {
            //cache
            remove(name.lowercase())

            reloadGui()

            KitsDataSQL.del(name.lowercase())
        }

        fun createNewKitData(name: String) {
            //cache
            put(
                name.lowercase(),
                KitData(name.lowercase(), name, emptyList(), 0L, 0)
            )
            reloadGui()

            KitsDataSQL.put(name.lowercase(), hashMapOf(KitsDataSQL.kitFakeName to name))
        }

        fun loadKitCache() {
            transaction(DataManager.sql) {
                for (values in KitsDataSQL.selectAll()) {
                    val kit = values[KitsDataSQL.kitName]
                    val kitFakeName = values[KitsDataSQL.kitFakeName]
                    val kitTime = values[KitsDataSQL.kitTime]
                    val item = values[KitsDataSQL.kitItems]
                    val weight = values[KitsDataSQL.kitWeight]
                    kitCacheV2[kit] = KitData(
                        kit,
                        kitFakeName,
                        Serializator.itemSerializer(item),
                        kitTime,
                        weight
                    )
                }
            }
        }
    }
}
