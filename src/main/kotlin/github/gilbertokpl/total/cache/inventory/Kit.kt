package github.gilbertokpl.total.cache.inventory

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.KitsData
import github.gilbertokpl.total.cache.data.KitsData.kitFakeName
import github.gilbertokpl.total.cache.data.KitsData.kitWeight
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.cache.internal.Data.kitInventoryCache
import github.gilbertokpl.total.cache.internal.Data.kitItemCache
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.ItemUtil
import github.gilbertokpl.total.util.MaterialUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

internal object Kit {

    // ────────────────────────────────────────────────────────────────
    //  CONSTANTES
    // ────────────────────────────────────────────────────────────────

    private object Slots {
        const val ITEMS_PER_PAGE = 27
        const val INVENTORY_SIZE_KITS = 36
        const val INVENTORY_SIZE_KIT_VIEW = 45

        // Slots de navegação (inventário de kits)
        const val BACK_BUTTON = 27
        val GLASS_SLOTS = 28..34
        const val NEXT_BUTTON = 35

        // Slots especiais (visualização de kit)
        const val KIT_BACK = 36
        const val KIT_EDIT = 40
        const val KIT_GET = 44
        val KIT_GLASS_SLOTS = (37..39) + (41..43)
    }

    private object Permissions {
        const val EDIT_KIT = "totalessentials.commands.editkit"
        const val BYPASS_COOLDOWN = "totalessentials.bypass.kitcatch"
        fun kitAccess(kit: String) = "totalessentials.commands.kit.$kit"
    }

    // ────────────────────────────────────────────────────────────────
    //  CACHE DE ITENS
    // ────────────────────────────────────────────────────────────────

    private val glassItem: ItemStack by lazy {
        ItemUtil.createItem(MaterialUtil["glass"]!!, "§eKIT", glowEffect = true)
    }

    private val backButtonItem: ItemStack by lazy {
        ItemUtil.createItem(Material.HOPPER, LangConfig.kitsInventoryIconBackName, glowEffect = true)
    }

    private val nextButtonItem: ItemStack by lazy {
        ItemUtil.createItem(Material.ARROW, LangConfig.kitsInventoryIconNextName, glowEffect = true)
    }

    private val editKitItem: ItemStack by lazy {
        ItemUtil.createItem(Material.CHEST, LangConfig.kitsInventoryIconEditKitName, glowEffect = true)
    }

    private val getKitItem: ItemStack by lazy {
        ItemUtil.createItem(Material.ARROW, LangConfig.kitsGetIcon, glowEffect = true)
    }

    private val noPermissionItem: ItemStack by lazy {
        ItemUtil.createItem(Material.ARROW, LangConfig.kitsGetIconNotCatch, LangConfig.kitsGetIconLoreNotPerm, true)
    }

    // ────────────────────────────────────────────────────────────────
    //  SETUP INICIAL
    // ────────────────────────────────────────────────────────────────

    fun setup() {
        val sortedKits = getSortedKits()
        if (sortedKits.isEmpty()) {
            kitItemCache = emptyMap()
            kitInventoryCache = emptyMap()
            return
        }

        val inventoryCache = mutableMapOf<Int, Inventory>()
        val itemCache = mutableMapOf<Int, String>()

        var currentPage = 1
        var currentSlot = 0
        var inventory = createKitsInventory(currentPage)

        sortedKits.forEach { kitKey ->
            val cacheIndex = calculateCacheIndex(currentSlot, currentPage)
            itemCache[cacheIndex] = kitKey

            inventory.setItem(currentSlot, createKitItem(kitKey))
            currentSlot++

            if (currentSlot == Slots.ITEMS_PER_PAGE) {
                applyNavigationBar(inventory, currentPage, hasNextPage = true)
                inventoryCache[currentPage] = inventory

                currentPage++
                currentSlot = 0
                inventory = createKitsInventory(currentPage)
            }
        }

        // Última página (pode não estar cheia)
        if (currentSlot > 0) {
            applyNavigationBar(inventory, currentPage, hasNextPage = false)
            inventoryCache[currentPage] = inventory
        }

        kitItemCache = itemCache.toMap()
        kitInventoryCache = inventoryCache.toMap()
    }

    private fun getSortedKits(): List<String> {
        return kitWeight.getMap()
            .filterValues { it != null }
            .entries
            .sortedByDescending { it.value }
            .map { it.key }
    }

    private fun calculateCacheIndex(slot: Int, page: Int): Int {
        return slot + 1 + (Slots.ITEMS_PER_PAGE * (page - 1))
    }

    // ────────────────────────────────────────────────────────────────
    //  CRIAÇÃO DE INVENTÁRIOS
    // ────────────────────────────────────────────────────────────────

    private fun createKitsInventory(page: Int): Inventory {
        return TotalEssentials.getInstance().server.createInventory(
            null,
            Slots.INVENTORY_SIZE_KITS,
            "§eKits $page"
        )
    }

    fun openKitInventory(kit: String, guiNumber: String, player: Player) {
        val inventory = TotalEssentials.getInstance().server.createInventory(
            null,
            Slots.INVENTORY_SIZE_KIT_VIEW,
            "§eKit $kit $guiNumber"
        )

        // Adiciona itens do kit
        KitsData.kitItems[kit]?.forEach { inventory.addItem(it) }

        // Adiciona barra de controle
        applyKitControlBar(inventory, kit, player)

        player.openInventory(inventory)
    }

    // ────────────────────────────────────────────────────────────────
    //  BARRAS DE NAVEGAÇÃO/CONTROLE
    // ────────────────────────────────────────────────────────────────

    private fun applyNavigationBar(inventory: Inventory, currentPage: Int, hasNextPage: Boolean) {
        // Botão voltar (só aparece se não for primeira página)
        inventory.setItem(Slots.BACK_BUTTON, if (currentPage > 1) backButtonItem else glassItem)

        // Vidros decorativos
        Slots.GLASS_SLOTS.forEach { inventory.setItem(it, glassItem) }

        // Botão próximo
        inventory.setItem(Slots.NEXT_BUTTON, if (hasNextPage) nextButtonItem else glassItem)
    }

    private fun applyKitControlBar(inventory: Inventory, kit: String, player: Player) {
        // Vidros decorativos
        Slots.KIT_GLASS_SLOTS.forEach { inventory.setItem(it, glassItem) }

        // Botão voltar
        inventory.setItem(Slots.KIT_BACK, backButtonItem)

        // Botão editar (só para admins)
        inventory.setItem(
            Slots.KIT_EDIT,
            if (player.hasPermission(Permissions.EDIT_KIT)) editKitItem else glassItem
        )

        // Botão pegar kit
        inventory.setItem(Slots.KIT_GET, createGetKitItem(kit, player))
    }

    // ────────────────────────────────────────────────────────────────
    //  CRIAÇÃO DE ITENS
    // ────────────────────────────────────────────────────────────────

    private fun createKitItem(kitKey: String): ItemStack {
        val kitName = kitFakeName[kitKey]?.takeIf { it.isNotEmpty() } ?: kitKey
        val baseItem = KitsData.kitItems[kitKey]?.firstOrNull() ?: ItemStack(Material.CHEST)

        return baseItem.clone().apply {
            itemMeta = itemMeta?.apply {
                ItemUtil.setDisplayName(this, LangConfig.kitsInventoryItemsName.replace("%kitrealname%", kitName))
                lore = LangConfig.kitsInventoryItemsLore.map { it.replace("%realname%", kitKey) }
            }
        }
    }

    private fun createGetKitItem(kit: String, player: Player): ItemStack {
        // Sem permissão
        if (!player.hasPermission(Permissions.kitAccess(kit))) {
            return noPermissionItem
        }

        // Com bypass de cooldown
        if (player.hasPermission(Permissions.BYPASS_COOLDOWN)) {
            return getKitItem
        }

        // Verifica cooldown
        val cooldownEnd = calculateCooldownEnd(kit, player)

        if (cooldownEnd <= System.currentTimeMillis()) {
            return getKitItem
        }

        return createCooldownItem(cooldownEnd)
    }

    private fun calculateCooldownEnd(kit: String, player: Player): Long {
        val lastUsed = PlayerData.kitsCache[player]?.get(kit) ?: 0L
        val kitCooldown = KitsData.kitTime[kit] ?: 0L
        return lastUsed + kitCooldown
    }

    private fun createCooldownItem(cooldownEnd: Long): ItemStack {
        val remainingTime = cooldownEnd - System.currentTimeMillis()
        val formattedTime = TotalEssentials.getCore()
            .getTime()
            .convertMillisToString(remainingTime, MainConfig.kitsUseShortTime)

        val lore = LangConfig.kitsGetIconLoreTime.map { it.replace("%time%", formattedTime) }

        return ItemUtil.createItem(Material.ARROW, LangConfig.kitsGetIconNotCatch, lore, true)
    }
}