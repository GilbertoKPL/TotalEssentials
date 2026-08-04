package github.gilbertokpl.total.placeholder

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.cache.inventory.Playtime
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.economy.MoneyManager
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

class TotalPlaceholderExpansion(
    private val plugin: TotalEssentials
) : PlaceholderExpansion() {

    override fun getIdentifier(): String = "totalessentials"

    override fun getAuthor(): String = plugin.description.authors.joinToString(", ").ifBlank { "Gilberto" }

    override fun getVersion(): String = plugin.description.version

    override fun persist(): Boolean = true

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        val normalized = params.lowercase()

        resolveMoneyTop(normalized)?.let { return it }
        resolvePlaytimeTop(normalized)?.let { return it }

        if (player == null) return ""

        return when (normalized) {
            "money" -> formatPlayerMoney(player)
            "money_raw" -> getPlayerMoney(player).toString()
            "nickname" -> PlayerData.nickCache[player]?.takeIf { it.isNotEmpty() } ?: player.name
            "vanished" -> if (PlayerData.vanishCache[player] == true) "yes" else "no"
            else -> null
        }
    }

    private fun resolveMoneyTop(params: String): String? {
        val match = MONEY_TOP_PATTERN.matchEntire(params) ?: return null
        val position = match.groupValues[1].toIntOrNull() ?: return ""
        val rankedPlayer = MoneyManager.getTopPlayer(position) ?: return ""

        return when (match.groupValues[2]) {
            "name" -> rankedPlayer.first
            "value" -> MoneyManager.formatMoney(rankedPlayer.second)
            "raw" -> rankedPlayer.second.toString()
            else -> ""
        }
    }

    private fun resolvePlaytimeTop(params: String): String? {
        val match = PLAYTIME_TOP_PATTERN.matchEntire(params) ?: return null
        val position = match.groupValues[1].toIntOrNull() ?: return ""
        val rankedPlayer = Playtime.getTopPlayer(position) ?: return ""

        return when (match.groupValues[2]) {
            "name" -> rankedPlayer.name
            "time" -> TotalEssentials.getCore().getTime()
                .convertMillisToString(rankedPlayer.time, true)
            "raw" -> rankedPlayer.time.toString()
            else -> ""
        }
    }

    private fun formatPlayerMoney(player: Player): String {
        return if (MainConfig.moneyActivated) {
            MoneyManager.formatMoney(getPlayerMoney(player))
        } else {
            MoneyManager.formatMoney(0.0)
        }
    }

    private fun getPlayerMoney(player: Player): Double {
        return PlayerData.moneyCache[player] ?: 0.0
    }

    private companion object {
        val MONEY_TOP_PATTERN = Regex("(?:money_top|moneytop)_(\\d+)_(name|value|raw)")
        val PLAYTIME_TOP_PATTERN = Regex("(?:playtime_top|playtimetop)_(\\d+)_(name|time|raw)")
    }
}
