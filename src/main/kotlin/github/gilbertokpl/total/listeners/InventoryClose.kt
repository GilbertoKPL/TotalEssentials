package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.data.KitsData
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.cache.data.VipData
import github.gilbertokpl.total.cache.data.test.LimitData
import github.gilbertokpl.total.cache.internal.Data
import github.gilbertokpl.total.cache.inventory.Kit
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.listeners.InventoryClick.Titles
import github.gilbertokpl.total.util.PlayerUtil
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

class InventoryClose : Listener {

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return

        if (MainConfig.kitsActivated) {
            if (handleKitEdit(event, player)) return
        }

        if (MainConfig.vipActivated) {
            handleVip(event, player)
            if (handleVipEdit(event, player)) return
        }

        if (MainConfig.limitActivated) {
            if (handleLimitEdit(event, player)) return
        }

        if (MainConfig.invseeActivated) {
            handleInvseeClose(event, player)
        }
    }

    private fun handleKitEdit(event: InventoryCloseEvent, player: Player): Boolean {
        val kitName = Data.playerEditKit[player] ?: return false

        Data.playerEditKit.remove(player)

        val items = extractItems(event)
        KitsData.kitItems[kitName, items] = true

        val displayName = KitsData.kitFakeName[kitName]?.takeIf { it.isNotEmpty() } ?: kitName

        PlayerUtil.sendMessage(
            player.name,
            LangConfig.kitsEditKitSuccess.replace("%kit%", displayName)
        )

        Kit.setup()
        return true
    }

    private fun handleVip(event: InventoryCloseEvent, player: Player) {
        val player = event.player as? Player ?: return

        val titleParts = getInventoryTitle(event) ?: return
        if (titleParts[0] != Titles.VIP) return

        // Itens que sobraram dentro do inventário
        val leftover = event.inventory.contents
            .filterNotNull()
            .toMutableList()

        // Itens que estavam no cache
        val cache = PlayerData.vipItems[player.name] ?: arrayListOf()

        // Junta tudo novamente no cache (inventário ∪ cache)
        cache.addAll(leftover)

        // Atualiza o cache real
        PlayerData.vipItems[player.name] = cache
    }

    private fun handleVipEdit(event: InventoryCloseEvent, player: Player): Boolean {
        val vipName = Data.playerVipEdit[player] ?: return false

        Data.playerVipEdit.remove(player)

        val items = extractItems(event)
        VipData.vipItems[vipName, items] = true

        PlayerUtil.sendMessage(
            player.name,
            LangConfig.VipsUpdateItems.replace("%vip%", vipName)
        )

        return true
    }

    private fun handleLimitEdit(event: InventoryCloseEvent, player: Player): Boolean {
        val limitName = Data.playerVipEdit[player] ?: return false

        val items = extractItems(event)
        LimitData.limitItems[limitName, items] = true

        return true
    }

    private fun handleInvseeClose(event: InventoryCloseEvent, player: Player) {
        if (event.inventory.type == InventoryType.PLAYER && PlayerData.inInvSee[player] != null) {
            PlayerData.inInvSee[player] = null
        }
    }

    private fun extractItems(event: InventoryCloseEvent): ArrayList<ItemStack> {
        return event.inventory.contents
            .filterNotNull()
            .toCollection(ArrayList())
    }

    private fun getInventoryTitle(event: InventoryCloseEvent): List<String>? {
        return try {
            event.view.title.split(" ")
        } catch (_: Exception) {
            null
        }
    }
}