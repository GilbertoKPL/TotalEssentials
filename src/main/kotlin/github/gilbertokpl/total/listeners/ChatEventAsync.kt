package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.KitsData
import github.gilbertokpl.total.cache.data.LoginData
import github.gilbertokpl.total.cache.internal.Data
import github.gilbertokpl.total.cache.inventory.Kit
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatEventAsync : Listener {

    companion object {
        private const val MAX_KIT_NAME_LENGTH = 16
        private val COLOR_CODE_REGEX = "&|§([0-9]|[a-f])".toRegex()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onAsyncPlayerChat(event: AsyncPlayerChatEvent) {
        val player = event.player

        if (!LoginData.isPlayerLoggedIn(player)) {
            event.isCancelled = true
            return
        }

        if (MainConfig.kitsActivated) {
            handleKitEdit(event, player)
        }
    }

    private fun handleKitEdit(event: AsyncPlayerChatEvent, player: Player) {
        val editData = Data.playerEditKitChat[player] ?: return

        Data.playerEditKitChat.remove(player)
        event.isCancelled = true

        val parts = editData.split("-", limit = 2)
        if (parts.size < 2) return

        val editType = parts[0]
        val kitName = parts[1]

        when (editType) {
            "time" -> handleTimeEdit(event.message, kitName, player)
            "name" -> handleNameEdit(event.message, kitName, player)
            "weight" -> handleWeightEdit(event.message, kitName, player)
        }
    }

    private fun handleTimeEdit(input: String, kitName: String, player: Player) {
        val timeUtil = TotalEssentials.getCore().getTime()
        val timeInMillis = timeUtil.convertStringToMillis(input)

        val formattedTime = timeUtil.convertMillisToString(
            timeInMillis,
            MainConfig.kitsUseShortTime
        )

        player.sendMessage(
            LangConfig.kitsEditKitTime.replace("%time%", formattedTime)
        )

        KitsData.kitTime[kitName] = timeInMillis
        sendSuccessMessage(player, kitName)
    }

    private fun handleNameEdit(input: String, kitName: String, player: Player) {
        val nameWithoutColors = input.replace(COLOR_CODE_REGEX, "")

        if (nameWithoutColors.length > MAX_KIT_NAME_LENGTH) {
            player.sendMessage(LangConfig.kitsNameLength)
            return
        }

        val coloredName = input.replace("&", "§")

        KitsData.kitFakeName[kitName] = coloredName
        sendSuccessMessage(player, kitName)
        Kit.setup()
    }

    private fun handleWeightEdit(input: String, kitName: String, player: Player) {
        val weight = input.toIntOrNull() ?: 0

        KitsData.kitWeight[kitName] = weight
        sendSuccessMessage(player, kitName)
        Kit.setup()
    }

    private fun sendSuccessMessage(player: Player, kitName: String) {
        player.sendMessage(
            LangConfig.kitsEditKitSuccess.replace("%kit%", kitName)
        )
    }
}