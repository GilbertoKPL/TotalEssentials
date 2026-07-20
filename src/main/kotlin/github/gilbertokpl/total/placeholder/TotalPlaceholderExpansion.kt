package github.gilbertokpl.total.placeholder

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
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
        if (player == null) return ""

        return when (params.lowercase()) {
            "money" -> formatPlayerMoney(player)
            "money_raw" -> getPlayerMoney(player).toString()
            "nickname" -> PlayerData.nickCache[player]?.takeIf { it.isNotEmpty() } ?: player.name
            "vanished" -> if (PlayerData.vanishCache[player] == true) "yes" else "no"
            else -> null
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
}
