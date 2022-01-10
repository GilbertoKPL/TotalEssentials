package github.gilbertokpl.essentialsk.data.objects

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.sql.KitDataSQLUtil
import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import github.gilbertokpl.essentialsk.tables.KitsDataSQL
import github.gilbertokpl.essentialsk.util.ItemUtil
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack

data class KitDataV2(
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
}
