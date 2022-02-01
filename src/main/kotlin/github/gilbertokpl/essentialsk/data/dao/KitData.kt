package github.gilbertokpl.essentialsk.data.dao

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.tables.KitsDataSQL
import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import github.gilbertokpl.essentialsk.util.ItemUtil
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.DataManager.del
import github.gilbertokpl.essentialsk.data.DataManager.put
import org.bukkit.command.CommandSender
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

        val toSave = ItemUtil.itemSerializer(i)

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

        fun delKitData(s: CommandSender? = null, name: String) {
            //cache
            KitData.remove(name.lowercase())

            reloadGui()

            KitsDataSQL.del(name.lowercase())

            s?.sendMessage(GeneralLang.kitsDelKitSuccess.replace("%kit%", name))
        }

        fun createNewKitData(s: CommandSender? = null, name: String) {
            //cache
            KitData.put(
                name.lowercase(),
                KitData(name.lowercase(), name, emptyList(), 0L, 0)
            )
            reloadGui()

            KitsDataSQL.put(name.lowercase(), hashMapOf(KitsDataSQL.kitFakeName to name))

            s?.sendMessage(
                GeneralLang.kitsCreateKitSuccess.replace(
                    "%kit%",
                    name.lowercase()
                )
            )
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
                        ItemUtil.itemSerializer(item),
                        kitTime,
                        weight
                    )
                }
            }
        }
    }
}
