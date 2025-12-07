package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.data.KitsData
import github.gilbertokpl.total.cache.data.LoginData
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.cache.data.ShopData
import github.gilbertokpl.total.cache.internal.Data
import github.gilbertokpl.total.cache.inventory.EditKit.editKitGui
import github.gilbertokpl.total.cache.inventory.EditKit.editKitGuiItems
import github.gilbertokpl.total.cache.inventory.Kit.openKitInventory
import github.gilbertokpl.total.cache.inventory.Shop
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.ItemUtil
import github.gilbertokpl.total.util.PermissionUtil
import github.gilbertokpl.total.util.PlayerUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

class InventoryClick : Listener {

    // ────────────────────────────────────────────────────────────────
    //  CONSTANTES
    // ────────────────────────────────────────────────────────────────

    private object Slots {
        const val ITEMS_PER_PAGE = 27

        // Kit view
        const val KIT_BACK = 36
        const val KIT_EDIT = 40
        const val KIT_GET = 44

        // Navegação geral
        const val NAV_BACK = 27
        const val NAV_NEXT = 35

        // Shop
        const val SHOP_CREATE = 30
        const val SHOP_TOGGLE = 32

        // EditKit
        const val EDIT_ITEMS = 10
        const val EDIT_TIME = 12
        const val EDIT_NAME = 14
        const val EDIT_WEIGHT = 16
    }

    object Titles {
        const val KIT_SINGLE = "§eKit"
        const val KIT_LIST = "§eKits"
        const val EDIT_KIT = "§eEditKit"
        const val SHOP = "§eSHOP"
        const val PLAYTIME = "§ePLAYTIME"
        const val VIP = "§eVipItens"
    }

    private object Permissions {
        const val EDIT_KIT = "totalessentials.commands.editkit"
        const val SHOP_SET = "totalessentials.commands.shop.set"
        const val BYPASS_SHIFT = "totalessentials.bypass.shiftcontainer"
        const val INVSEE = "totalessentials.commands.invsee"
        const val INVSEE_ADMIN = "totalessentials.commands.invsee.admin"
    }

    // ────────────────────────────────────────────────────────────────
    //  HANDLER PRINCIPAL
    // ────────────────────────────────────────────────────────────────

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return

        if (!LoginData.isPlayerLoggedIn(player)) {
            event.isCancelled = true
            return
        }

        // Slot especial que sempre permite interação
        if (event.slot == 45) return

        // Processa GUIs específicas
        if (MainConfig.kitsActivated) {
            if (handleEditKitGui(event, player)) return
            if (handleKitGui(event, player)) return
        }

        if (MainConfig.shopActivated) {
            if (handleShopGui(event, player)) return
        }

        if (MainConfig.playtimeActivated) {
            if (handlePlaytimeGui(event, player)) return
        }

        if (MainConfig.vipActivated) {
            if (handleVipGui(event, player)) return
        }

        // Funcionalidades gerais
        if (MainConfig.containersBlockShiftEnable) {
            blockShiftClick(event, player)
        }

        if (MainConfig.addonsColorInAnvil) {
            handleAnvilColor(event, player)
        }

        if (MainConfig.invseeActivated) {
            handleInvsee(event, player)
        }
    }

    // ────────────────────────────────────────────────────────────────
    //  VIP GUI
    // ────────────────────────────────────────────────────────────────

    private fun handleVipGui(event: InventoryClickEvent, player: Player): Boolean {
        val titleParts = getInventoryTitle(event) ?: return false
        if (titleParts[0] != Titles.VIP) return false

        event.isCancelled = true

        val clickedItem = event.currentItem ?: return true
        if (clickedItem.type == Material.AIR) return true
        if (!event.click.isLeftClick) return true
        if (event.rawSlot != event.slot) return true

        val freeSlots = (0..35).count { player.inventory.getItem(it) == null }
        if (freeSlots == 0) return true

        // pega item
        player.inventory.addItem(clickedItem)
        event.currentItem = ItemStack(Material.AIR)

        // pega cache FIFO do player
        val cache = PlayerData.vipItems[player] ?: arrayListOf()

        // se tiver mais itens na fila → recarrega o slot
        if (cache.isNotEmpty()) {
            val nextItem = cache.removeAt(0)
            event.inventory.setItem(event.slot, nextItem)
        }

        // salva novo cache
        PlayerData.vipItems[player] = cache

        return true
    }

    // ────────────────────────────────────────────────────────────────
    //  PLAYTIME GUI
    // ────────────────────────────────────────────────────────────────

    private fun handlePlaytimeGui(event: InventoryClickEvent, player: Player): Boolean {
        val titleParts = getInventoryTitle(event) ?: return false
        if (titleParts[0] != Titles.PLAYTIME) return false

        event.isCancelled = true

        val currentPage = titleParts.getOrNull(1)?.toIntOrNull() ?: return true

        when (event.slot) {
            Slots.NAV_BACK -> navigateToPage(player, Data.playTimeInventoryCache, currentPage - 1)
            Slots.NAV_NEXT -> navigateToPage(player, Data.playTimeInventoryCache, currentPage + 1)
        }

        return true
    }

    // ────────────────────────────────────────────────────────────────
    //  SHOP GUI
    // ────────────────────────────────────────────────────────────────

    private fun handleShopGui(event: InventoryClickEvent, player: Player): Boolean {
        val titleParts = getInventoryTitle(event) ?: return false
        if (titleParts[0] != Titles.SHOP) return false

        event.isCancelled = true

        val currentPage = titleParts.getOrNull(1)?.toIntOrNull() ?: return true
        val slot = event.slot

        when {
            slot < Slots.ITEMS_PER_PAGE -> handleShopSelection(slot, currentPage, player)
            slot == Slots.NAV_BACK && currentPage > 1 -> {
                navigateToPage(player, Data.shopInventoryCache, currentPage - 1)
            }
            slot == Slots.SHOP_CREATE && player.hasPermission(Permissions.SHOP_SET) -> {
                createShop(player)
            }
            slot == Slots.SHOP_TOGGLE && player.hasPermission(Permissions.SHOP_SET) -> {
                toggleShop(player)
            }
            slot == Slots.NAV_NEXT -> {
                navigateToPage(player, Data.shopInventoryCache, currentPage + 1)
            }
        }

        return true
    }

    private fun handleShopSelection(slot: Int, page: Int, player: Player) {
        val shopIndex = calculateCacheIndex(slot, page)
        val shopLocation = Data.shopItemCache[shopIndex] ?: return

        if (ShopData.shopOpen[shopLocation] != true) {
            player.sendMessage(LangConfig.shopClosedMessage)
            return
        }

        PlayerUtil.shopTeleport(player, shopLocation)

        if (!shopLocation.equals(player.name, ignoreCase = true)) {
            ShopData.shopVisits[shopLocation] = (ShopData.shopVisits[shopLocation] ?: 0) + 1
        }
    }

    private fun createShop(player: Player) {
        ShopData.createNewShop(player.location, player)
        player.sendMessage(LangConfig.shopCreateShopSuccess)
        Shop.setup()
        player.closeInventory()
    }

    private fun toggleShop(player: Player) {
        if (!ShopData.checkIfShopExists(player.name.lowercase())) return

        val newState = !(ShopData.shopOpen[player] ?: false)
        ShopData.shopOpen[player] = newState

        val statusMessage = if (newState) LangConfig.shopOpen else LangConfig.shopClosed
        player.sendMessage(LangConfig.shopSwitchMessage.replace("%open%", statusMessage))

        Shop.setup()
        player.closeInventory()
    }

    // ────────────────────────────────────────────────────────────────
    //  KIT GUI
    // ────────────────────────────────────────────────────────────────

    private fun handleKitGui(event: InventoryClickEvent, player: Player): Boolean {
        val titleParts = getInventoryTitle(event) ?: return false

        return when (titleParts[0]) {
            Titles.KIT_SINGLE -> {
                event.isCancelled = true  // CANCELA PRIMEIRO!
                handleSingleKitGui(event, titleParts, player)
                true
            }
            Titles.KIT_LIST -> {
                event.isCancelled = true  // CANCELA PRIMEIRO!
                handleKitListGui(event, titleParts, player)
                true
            }
            else -> false
        }
    }

    private fun handleSingleKitGui(
        event: InventoryClickEvent,
        titleParts: List<String>,
        player: Player
    ) {
        val clickedItem = event.currentItem ?: return
        if (clickedItem.type == Material.AIR) return

        val meta = clickedItem.itemMeta ?: return
        val kitName = titleParts.getOrNull(1) ?: return
        val page = titleParts.getOrNull(2) ?: return

        when (event.slot) {
            Slots.KIT_BACK -> {
                page.toIntOrNull()?.let { navigateToPage(player, Data.kitInventoryCache, it) }
            }
            Slots.KIT_EDIT -> {
                if (meta.displayName == LangConfig.kitsInventoryIconEditKitName &&
                    player.hasPermission(Permissions.EDIT_KIT)) {
                    editKitGui(player, kitName)
                }
            }
            Slots.KIT_GET -> {
                when (meta.displayName) {
                    LangConfig.kitsGetIcon -> {
                        ItemUtil.pickupKit(player, kitName.lowercase())
                        player.closeInventory()
                    }
                    LangConfig.kitsGetIconNotCatch -> {
                        openKitInventory(kitName, page, player)
                    }
                }
            }
        }
    }

    private fun handleKitListGui(
        event: InventoryClickEvent,
        titleParts: List<String>,
        player: Player
    ) {
        val currentPage = titleParts.getOrNull(1)?.toIntOrNull() ?: return
        val slot = event.slot

        when {
            slot < Slots.ITEMS_PER_PAGE -> {
                val kitIndex = calculateCacheIndex(slot, currentPage)
                Data.kitItemCache[kitIndex]?.let { kit ->
                    openKitInventory(kit, currentPage.toString(), player)
                }
            }
            slot == Slots.NAV_BACK && currentPage > 1 -> {
                navigateToPage(player, Data.kitInventoryCache, currentPage - 1)
            }
            slot == Slots.NAV_NEXT -> {
                navigateToPage(player, Data.kitInventoryCache, currentPage + 1)
            }
        }
    }

    // ────────────────────────────────────────────────────────────────
    //  EDIT KIT GUI
    // ────────────────────────────────────────────────────────────────

    private fun handleEditKitGui(event: InventoryClickEvent, player: Player): Boolean {
        val titleParts = getInventoryTitle(event) ?: return false
        if (!titleParts[0].equals(Titles.EDIT_KIT, ignoreCase = true)) return false

        event.isCancelled = true

        val kitName = titleParts.getOrNull(1) ?: return true

        when (event.slot) {
            Slots.EDIT_ITEMS -> {
                player.closeInventory()
                KitsData.kitItems[kitName]?.let { items ->
                    editKitGuiItems(player, kitName, items)
                    Data.playerEditKit[player] = kitName
                }
            }
            Slots.EDIT_TIME -> {
                player.closeInventory()
                player.sendMessage(LangConfig.kitsEditKitInventoryTimeMessage)
                Data.playerEditKitChat[player] = "time-$kitName"
            }
            Slots.EDIT_NAME -> {
                player.closeInventory()
                player.sendMessage(LangConfig.kitsEditKitInventoryNameMessage)
                Data.playerEditKitChat[player] = "name-$kitName"
            }
            Slots.EDIT_WEIGHT -> {
                player.closeInventory()
                player.sendMessage(LangConfig.kitsEditKitInventoryWeightMessage)
                Data.playerEditKitChat[player] = "weight-$kitName"
            }
        }

        return true
    }

    // ────────────────────────────────────────────────────────────────
    //  FUNCIONALIDADES GERAIS
    // ────────────────────────────────────────────────────────────────

    private fun blockShiftClick(event: InventoryClickEvent, player: Player) {
        val clickedItem = event.currentItem ?: return
        if (clickedItem.type == Material.AIR) return
        if (!event.click.isShiftClick) return
        if (player.hasPermission(Permissions.BYPASS_SHIFT)) return

        val inventoryType = event.inventory.type.name.lowercase()
        if (MainConfig.containersBlockShift.contains(inventoryType)) {
            player.sendMessage(LangConfig.generalNotPermAction)
            event.isCancelled = true
        }
    }

    private fun handleAnvilColor(event: InventoryClickEvent, player: Player) {
        if (event.inventory.type != InventoryType.ANVIL) return
        if (event.slotType != InventoryType.SlotType.RESULT) return

        val resultItem = event.currentItem ?: return
        if (resultItem.type == Material.AIR) return

        val meta = resultItem.itemMeta ?: return
        if (!meta.hasDisplayName()) return

        val currentName = meta.displayName
        val originalItem = event.inventory.getItem(0) ?: return
        val originalMeta = originalItem.itemMeta ?: return

        // Preserva cores originais se o nome não foi alterado
        if (originalMeta.hasDisplayName()) {
            val originalNameWithoutColors = originalMeta.displayName.replace("§", "")
            if (currentName == originalNameWithoutColors) {
                try {
                    ItemUtil.setDisplayName(meta, originalMeta.displayName)
                } catch (_: NoSuchMethodError) {
                    meta.setDisplayName(originalMeta.displayName)
                }
                resultItem.itemMeta = meta
                event.currentItem = resultItem
                return
            }
        }

        ItemUtil.setDisplayName(meta, PermissionUtil.colorPermission(player, currentName))
        resultItem.itemMeta = meta
        event.currentItem = resultItem
    }

    private fun handleInvsee(event: InventoryClickEvent, player: Player) {
        if (event.inventory.type != InventoryType.PLAYER) return

        val targetPlayer = PlayerData.inInvSee[player] ?: return

        if (!targetPlayer.isOnline) {
            player.closeInventory()
            player.sendMessage(LangConfig.invseePlayerLeave)
            return
        }

        if (player.hasPermission(Permissions.INVSEE) &&
            !player.hasPermission(Permissions.INVSEE_ADMIN)) {
            event.isCancelled = true
        }
    }

    // ────────────────────────────────────────────────────────────────
    //  HELPERS
    // ────────────────────────────────────────────────────────────────

    private fun getInventoryTitle(event: InventoryClickEvent): List<String>? {
        return try {
            event.view.title.split(" ")
        } catch (_: Exception) {
            null
        }
    }

    private fun calculateCacheIndex(slot: Int, page: Int): Int {
        return (slot + 1) + ((page - 1) * Slots.ITEMS_PER_PAGE)
    }

    private fun <T> navigateToPage(player: Player, cache: Map<Int, T>, page: Int) {
        if (page < 1) return
        (cache[page] as? org.bukkit.inventory.Inventory)?.let { player.openInventory(it) }
    }
}