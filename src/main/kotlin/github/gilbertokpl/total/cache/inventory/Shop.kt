package github.gilbertokpl.total.cache.inventory

import github.gilbertokpl.total.cache.data.ShopData
import github.gilbertokpl.total.cache.internal.Data
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.util.ItemUtil
import github.gilbertokpl.total.util.MaterialUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object Shop {

    // ────────────────────────────────────────────────────────────────
    //  CONSTANTES
    // ────────────────────────────────────────────────────────────────

    private object Slots {
        const val ITEMS_PER_PAGE = 27
        const val INVENTORY_SIZE = 36

        // Navegação
        const val BACK_BUTTON = 27
        val GLASS_SLOTS = 28..34
        const val NEXT_BUTTON = 35
    }

    // ────────────────────────────────────────────────────────────────
    //  CACHE DE ITENS
    // ────────────────────────────────────────────────────────────────

    val glassItem: ItemStack by lazy {
        ItemUtil.createItem(MaterialUtil["glass"]!!, "§eSHOP", glowEffect = true)
    }

    private val backButtonItem: ItemStack by lazy {
        ItemUtil.createItem(Material.HOPPER, LangConfig.shopInventoryIconBackName, glowEffect = true)
    }

    private val nextButtonItem: ItemStack by lazy {
        ItemUtil.createItem(Material.ARROW, LangConfig.shopInventoryIconNextName, glowEffect = true)
    }

    // ────────────────────────────────────────────────────────────────
    //  SETUP
    // ────────────────────────────────────────────────────────────────

    fun setup() {
        val sortedShops = getSortedShops()
        if (sortedShops.isEmpty()) {
            Data.shopInventoryCache = emptyMap()
            Data.shopItemCache = emptyMap()
            return
        }

        val inventoryCache = linkedMapOf<Int, Inventory>()
        val itemCache = linkedMapOf<Int, String>()

        var currentPage = 1
        var currentSlot = 0
        var inventory = createShopInventory(currentPage)

        sortedShops.forEach { (shopKey, visits) ->
            val cacheIndex = calculateCacheIndex(currentSlot, currentPage)
            itemCache[cacheIndex] = shopKey

            inventory.setItem(currentSlot, createShopItem(shopKey, visits ?: 0))
            currentSlot++

            if (currentSlot == Slots.ITEMS_PER_PAGE) {
                applyNavigationBar(inventory, currentPage, hasNextPage = true)
                inventoryCache[currentPage] = inventory

                currentPage++
                currentSlot = 0
                inventory = createShopInventory(currentPage)
            }
        }

        // Última página (pode não estar cheia)
        if (currentSlot > 0) {
            applyNavigationBar(inventory, currentPage, hasNextPage = false)
            inventoryCache[currentPage] = inventory
        }

        Data.shopInventoryCache = inventoryCache.toMap()
        Data.shopItemCache = itemCache.toMap()
    }

    private fun getSortedShops(): List<Pair<String, Int?>> {
        return ShopData.shopVisits.getMap()
            .toList()
            .sortedByDescending { it.second }
    }

    private fun calculateCacheIndex(slot: Int, page: Int): Int {
        return slot + 1 + ((page - 1) * Slots.ITEMS_PER_PAGE)
    }

    // ────────────────────────────────────────────────────────────────
    //  CRIAÇÃO DE INVENTÁRIOS
    // ────────────────────────────────────────────────────────────────

    private fun createShopInventory(page: Int): Inventory {
        return Bukkit.createInventory(null, Slots.INVENTORY_SIZE, "§eSHOP $page")
    }

    // ────────────────────────────────────────────────────────────────
    //  NAVEGAÇÃO
    // ────────────────────────────────────────────────────────────────

    private fun applyNavigationBar(inventory: Inventory, currentPage: Int, hasNextPage: Boolean) {
        // Botão voltar
        inventory.setItem(
            Slots.BACK_BUTTON,
            if (currentPage > 1) backButtonItem else glassItem
        )

        // Vidros decorativos
        Slots.GLASS_SLOTS.forEach { inventory.setItem(it, glassItem) }

        // Botão próximo
        inventory.setItem(
            Slots.NEXT_BUTTON,
            if (hasNextPage) nextButtonItem else glassItem
        )
    }

    // ────────────────────────────────────────────────────────────────
    //  CRIAÇÃO DE ITENS
    // ────────────────────────────────────────────────────────────────

    private fun createShopItem(shopKey: String, visits: Int): ItemStack {
        @Suppress("DEPRECATION")
        val item = ItemStack(MaterialUtil["head"]!!, 1, SkullType.PLAYER.ordinal.toShort())

        return item.apply {
            itemMeta = itemMeta?.apply {
                ItemUtil.setDisplayName(this, formatShopName(shopKey))
                lore = createShopLore(shopKey, visits)
            }
        }
    }

    private fun formatShopName(shopKey: String): String {
        return LangConfig.shopInventoryItemsName.replace("%player%", shopKey)
    }

    private fun createShopLore(shopKey: String, visits: Int): List<String> {
        val isOpen = ShopData.shopOpen[shopKey] ?: false
        val statusText = if (isOpen) LangConfig.shopOpen else LangConfig.shopClosed

        return LangConfig.shopInventoryItemsLore.map {
            it.replace("%visits%", visits.toString())
                .replace("%open%", statusText)
        }
    }
}