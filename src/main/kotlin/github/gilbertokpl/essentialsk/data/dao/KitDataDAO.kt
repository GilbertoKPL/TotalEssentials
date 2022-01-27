package github.gilbertokpl.essentialsk.data.dao

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.tables.KitsDataSQL
import github.gilbertokpl.essentialsk.data.util.KitDataSQLUtil
import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import github.gilbertokpl.essentialsk.util.ItemUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

internal data class KitDataDAO(
    val name: String,
    var fakeName: String,
    var items: List<ItemStack>,
    var time: Long,
    var weight: Int
) {

    fun setWeight(w: Int, s: CommandSender? = null) {
        //cache
        weight = w
        reloadGui()

        //sql
        KitDataSQLUtil.kitHelper(
            KitsDataSQL.kitWeight,
            weight,
            GeneralLang.kitsEditKitSuccess.replace("%kit%", name),
            name,
            s
        )
    }

    fun setFakeName(fN: String, s: CommandSender? = null) {
        //cache
        fakeName = fN
        reloadGui()

        //sql
        KitDataSQLUtil.kitHelper(
            KitsDataSQL.kitFakeName,
            fakeName,
            GeneralLang.kitsEditKitSuccess.replace("%kit%", name),
            name,
            s
        )
    }

    fun setItems(i: List<ItemStack>, s: CommandSender? = null) {
        //cache
        items = i
        reloadGui()

        val toSave = ItemUtil.itemSerializer(i)

        //sql
        KitDataSQLUtil.kitHelper(
            KitsDataSQL.kitItems,
            toSave,
            GeneralLang.kitsEditKitSuccess.replace("%kit%", name),
            name,
            s
        )
    }

    fun setTime(t: Long, s: CommandSender? = null) {
        //cache
        time = t
        reloadGui()

        //sql
        KitDataSQLUtil.kitHelper(
            KitsDataSQL.kitTime,
            t,
            GeneralLang.kitsEditKitSuccess.replace("%kit%", name),
            name,
            s
        )
    }

    private fun reloadGui() {
        KitGuiInventory.setup()
    }

    companion object {

        private val kitCacheV2 = mutableMapOf<String, KitDataDAO>()

        operator fun get(warp: String) = kitCacheV2[warp.lowercase()]

        fun remove(warp: String) = kitCacheV2.remove(warp.lowercase())

        fun put(warp: String, data: KitDataDAO) = kitCacheV2.put(warp.lowercase(), data)

        fun getList() = kitCacheV2.map { it.key }

        fun getMap() = kitCacheV2

        fun loadKitCache() {
            transaction(SqlUtil.sql) {
                for (values in KitsDataSQL.selectAll()) {
                    val kit = values[KitsDataSQL.kitName]
                    val kitFakeName = values[KitsDataSQL.kitFakeName]
                    val kitTime = values[KitsDataSQL.kitTime]
                    val item = values[KitsDataSQL.kitItems]
                    val weight = values[KitsDataSQL.kitWeight]
                    kitCacheV2[kit] = KitDataDAO(
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
