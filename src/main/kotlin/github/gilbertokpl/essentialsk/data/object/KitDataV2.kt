package github.gilbertokpl.essentialsk.data.`object`

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
        KitDataSQLUtil.getInstance().kitHelper(
            KitsDataSQL.kitWeight,
            weight,
            GeneralLang.getInstance().kitsEditKitSuccess.replace("%kit%", name),
            name,
            s
        )
    }

    fun setFakeName(fN: String, s: CommandSender? = null) {
        //cache
        fakeName = fN
        reloadGui()

        //sql
        KitDataSQLUtil.getInstance().kitHelper(
            KitsDataSQL.kitFakeName,
            fakeName,
            GeneralLang.getInstance().kitsEditKitSuccess.replace("%kit%", name),
            name,
            s
        )
    }

    fun setItems(i: List<ItemStack>, s: CommandSender? = null) {
        //cache
        items = i
        reloadGui()

        val toSave = ItemUtil.getInstance().itemSerializer(i)

        //sql
        KitDataSQLUtil.getInstance().kitHelper(
            KitsDataSQL.kitItems,
            toSave,
            GeneralLang.getInstance().kitsEditKitSuccess.replace("%kit%", name),
            name,
            s
        )
    }

    fun setTime(t: Long, s: CommandSender? = null) {
        //cache
        time = t
        reloadGui()

        //sql
        KitDataSQLUtil.getInstance().kitHelper(
            KitsDataSQL.kitTime,
            t,
            GeneralLang.getInstance().kitsEditKitSuccess.replace("%kit%", name),
            name,
            s
        )
    }

    private fun reloadGui() {
        KitGuiInventory.setup()
    }
}