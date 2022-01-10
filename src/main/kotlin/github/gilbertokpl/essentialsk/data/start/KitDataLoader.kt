package github.gilbertokpl.essentialsk.data.start

import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.objects.KitDataV2
import github.gilbertokpl.essentialsk.tables.KitsDataSQL
import github.gilbertokpl.essentialsk.util.ItemUtil
import github.gilbertokpl.essentialsk.util.SqlUtil
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object KitDataLoader {

    fun loadKitCache() {
        transaction(SqlUtil.sql) {
            for (values in KitsDataSQL.selectAll()) {
                val kit = values[KitsDataSQL.kitName]
                val kitFakeName = values[KitsDataSQL.kitFakeName]
                val kitTime = values[KitsDataSQL.kitTime]
                val item = values[KitsDataSQL.kitItems]
                val weight = values[KitsDataSQL.kitWeight]
                DataManager.kitCacheV2[kit] = KitDataV2(
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
