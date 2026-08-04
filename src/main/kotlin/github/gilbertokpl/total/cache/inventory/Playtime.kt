package github.gilbertokpl.total.cache.inventory

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.cache.internal.Data
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.ItemUtil
import github.gilbertokpl.total.util.MaterialUtil
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object Playtime {

    data class RankedPlayer(val name: String, val time: Long)

    @Volatile
    private var rankingSnapshot: List<RankedPlayer> = emptyList()

    @Volatile
    private var rankingInitialized = false

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

    private object Limits {
        const val MAX_PLAYERS = 135
        const val TOP_PLAYERS = 10
    }

    // ────────────────────────────────────────────────────────────────
    //  CACHE DE ITENS
    // ────────────────────────────────────────────────────────────────

    private val glassItem: ItemStack by lazy {
        ItemUtil.createItem(MaterialUtil["glass"]!!, "§ePLAYTIME", glowEffect = true)
    }

    private val backButtonItem: ItemStack by lazy {
        ItemUtil.createItem(Material.HOPPER, LangConfig.playtimeInventoryIconBackName, glowEffect = true)
    }

    private val nextButtonItem: ItemStack by lazy {
        ItemUtil.createItem(Material.ARROW, LangConfig.playtimeInventoryIconNextName, glowEffect = true)
    }

    // ────────────────────────────────────────────────────────────────
    //  SETUP
    // ────────────────────────────────────────────────────────────────

    fun setup() {
        val sortedPlayers = getSortedPlayers()
        rankingSnapshot = sortedPlayers
            .take(Limits.TOP_PLAYERS)
            .map { (playerName, time) -> RankedPlayer(playerName, time) }
        rankingInitialized = true

        if (sortedPlayers.isEmpty()) {
            Data.playTimeInventoryCache = emptyMap()
            return
        }

        val inventoryCache = linkedMapOf<Int, Inventory>()
        var currentPage = 1
        var currentSlot = 0
        var inventory = createPlaytimeInventory(currentPage)

        sortedPlayers.forEach { (playerName, time) ->
            inventory.setItem(currentSlot, createHeadItem(playerName, time))
            currentSlot++

            if (currentSlot == Slots.ITEMS_PER_PAGE) {
                applyNavigationBar(inventory, currentPage, hasNextPage = true)
                inventoryCache[currentPage] = inventory

                currentPage++
                currentSlot = 0
                inventory = createPlaytimeInventory(currentPage)
            }
        }

        // Última página (pode não estar cheia)
        if (currentSlot > 0) {
            applyNavigationBar(inventory, currentPage, hasNextPage = false)
            inventoryCache[currentPage] = inventory
        }

        Data.playTimeInventoryCache = inventoryCache.toMap()
    }

    fun getTopPlayer(position: Int): RankedPlayer? {
        if (!MainConfig.playtimeActivated || position !in 1..Limits.TOP_PLAYERS) return null
        if (!rankingInitialized) {
            rankingSnapshot = getSortedPlayers()
                .take(Limits.TOP_PLAYERS)
                .map { (playerName, time) -> RankedPlayer(playerName, time) }
            rankingInitialized = true
        }
        return rankingSnapshot.getOrNull(position - 1)
    }

    private fun getSortedPlayers(): List<Pair<String, Long>> {
        return PlayerData.playTimeCache.getMap()
            .mapNotNull { (playerName, savedTime) ->
                savedTime?.let { playerName to calculateTotalTime(playerName, it) }
            }
            .sortedByDescending { it.second }
            .take(Limits.MAX_PLAYERS)
    }

    // ────────────────────────────────────────────────────────────────
    //  CRIAÇÃO DE INVENTÁRIOS
    // ────────────────────────────────────────────────────────────────

    private fun createPlaytimeInventory(page: Int): Inventory {
        return TotalEssentials.getInstance().server.createInventory(
            null,
            Slots.INVENTORY_SIZE,
            "§ePLAYTIME $page"
        )
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

    fun createHeadItem(playerName: String, time: Long): ItemStack {
        @Suppress("DEPRECATION")
        val item = ItemStack(MaterialUtil["head"]!!, 1, SkullType.PLAYER.ordinal.toShort())

        return item.apply {
            itemMeta = itemMeta?.apply {
                ItemUtil.setDisplayName(this, formatPlayerName(playerName))
                lore = createTimeLore(time)
            }
        }
    }

    private fun formatPlayerName(playerName: String): String {
        return LangConfig.playtimeInventoryItemsName.replace("%player%", playerName)
    }

    private fun createTimeLore(time: Long): List<String> {
        val formattedTime = formatTime(time)

        return LangConfig.playtimeInventoryItemsLore.map {
            it.replace("%time%", formattedTime)
        }
    }

    private fun calculateTotalTime(playerName: String, savedTime: Long): Long {
        val sessionStart = PlayerData.playtimeLocal[playerName] ?: 0L
        val currentSessionTime = if (sessionStart != 0L) {
            System.currentTimeMillis() - sessionStart
        } else {
            0L
        }
        return savedTime + currentSessionTime
    }

    private fun formatTime(millis: Long): String {
        return TotalEssentials.getCore()
            .getTime()
            .convertMillisToString(millis, true)
    }
}
