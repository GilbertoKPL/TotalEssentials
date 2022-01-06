package github.gilbertokpl.essentialsk.data.start

import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.`object`.KitDataV2
import github.gilbertokpl.essentialsk.manager.IInstance
import github.gilbertokpl.essentialsk.tables.KitsDataSQL
import github.gilbertokpl.essentialsk.util.ItemUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class KitDataLoader {

    fun loadKitCache() {
        transaction(SqlUtil.getInstance().sql) {
            for (values in KitsDataSQL.selectAll()) {
                val kit = values[KitsDataSQL.kitName]
                val kitFakeName = values[KitsDataSQL.kitFakeName]
                val kitTime = values[KitsDataSQL.kitTime]
                val item = values[KitsDataSQL.kitItems]
                val weight = values[KitsDataSQL.kitWeight]
                DataManager.getInstance().kitCacheV2[kit] = KitDataV2(
                    kit,
                    kitFakeName,
                    ItemUtil.getInstance().itemSerializer(item),
                    kitTime,
                    weight
                )
            }
        }
    }

    companion object : IInstance<KitDataLoader> {
        private val instance = createInstance()
        override fun createInstance(): KitDataLoader = KitDataLoader()
        override fun getInstance(): KitDataLoader {
            return instance
        }
    }
}