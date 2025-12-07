package github.gilbertokpl.total.cache.inventory

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.internal.Data
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.util.ItemUtil
import github.gilbertokpl.total.util.MaterialUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

internal object EditKit {

    // ────────────────────────────────────────────────────────────────
    //  CONSTANTES
    // ────────────────────────────────────────────────────────────────

    private object Slots {
        const val EDIT_KIT_SIZE = 27
        const val EDIT_KIT_ITEMS_SIZE = 36

        // Slots de opções
        const val ITEMS = 10
        const val TIME = 12
        const val NAME = 14
        const val WEIGHT = 16
    }

    // ────────────────────────────────────────────────────────────────
    //  CACHE DE ITENS (lazy para garantir que LangConfig já carregou)
    // ────────────────────────────────────────────────────────────────

    private val glassItem: ItemStack by lazy {
        ItemUtil.createItem(MaterialUtil["glass"] ?: Material.GLASS, "", glowEffect = false)
    }

    private val itemsButton: ItemStack by lazy {
        ItemUtil.createItem(
            Material.CHEST,
            LangConfig.kitsEditKitInventoryItemsName,
            LangConfig.kitsEditKitInventoryItemsLore
        )
    }

    private val timeButton: ItemStack by lazy {
        ItemUtil.createItem(
            MaterialUtil["clock"] ?: Material.CLOCK,
            LangConfig.kitsEditKitInventoryTimeName,
            LangConfig.kitsEditKitInventoryTimeLore
        )
    }

    private val nameButton: ItemStack by lazy {
        ItemUtil.createItem(
            Material.BOOK,
            LangConfig.kitsEditKitInventoryNameName,
            LangConfig.kitsEditKitInventoryNameLore
        )
    }

    private val weightButton: ItemStack by lazy {
        ItemUtil.createItem(
            MaterialUtil["feather"] ?: Material.FEATHER,
            LangConfig.kitsEditKitInventoryWeightName,
            LangConfig.kitsEditKitInventoryWeightLore
        )
    }

    // ────────────────────────────────────────────────────────────────
    //  SETUP
    // ────────────────────────────────────────────────────────────────

    fun setup() {
        Data.editKitItemCache = buildEditKitLayout()
    }

    private fun buildEditKitLayout(): MutableMap<Int, ItemStack> {
        return (0 until Slots.EDIT_KIT_SIZE).associateWith { slot ->
            when (slot) {
                Slots.ITEMS -> itemsButton
                Slots.TIME -> timeButton
                Slots.NAME -> nameButton
                Slots.WEIGHT -> weightButton
                else -> glassItem
            }
        }.toMutableMap()
    }

    // ────────────────────────────────────────────────────────────────
    //  ABERTURA DE INVENTÁRIOS
    // ────────────────────────────────────────────────────────────────

    fun editKitGui(player: Player, kit: String) {
        val inventory = TotalEssentials.getInstance().server.createInventory(
            null,
            Slots.EDIT_KIT_SIZE,
            "§eEditKit $kit"
        )

        Data.editKitItemCache.forEach { (slot, item) ->
            inventory.setItem(slot, item)
        }

        player.openInventory(inventory)
    }

    fun editKitGuiItems(player: Player, kit: String, items: List<ItemStack>) {
        val inventory = TotalEssentials.getInstance().server.createInventory(
            null,
            Slots.EDIT_KIT_ITEMS_SIZE,
            kit
        )

        items.forEach { inventory.addItem(it) }

        player.openInventory(inventory)
    }
}