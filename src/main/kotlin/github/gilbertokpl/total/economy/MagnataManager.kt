package github.gilbertokpl.total.economy

import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.ServerUtil
import org.bukkit.entity.Player

object MagnataManager {

    fun sendJoinMessage(player: Player) {
        if (!MainConfig.addonsMagnataActivated || !MainConfig.addonsMagnataAnnounceOnJoin) return
        if (!isMagnata(player)) return

        val balance = getBalance(player) ?: return
        MainConfig.addonsMagnataJoinMessages.forEach { message ->
            ServerUtil.broadcastMessage(format(message, player, balance))
        }
    }

    fun getPrefix(player: Player): String {
        if (!MainConfig.addonsMagnataActivated || !isMagnata(player)) {
            return MainConfig.addonsMagnataEmptyPrefix
        }

        val balance = getBalance(player) ?: return MainConfig.addonsMagnataEmptyPrefix
        return format(MainConfig.addonsMagnataPrefix, player, balance)
    }

    private fun isMagnata(player: Player): Boolean {
        val richestPlayer = getRichestPlayerName() ?: return false
        return richestPlayer.equals(player.name, ignoreCase = true)
    }

    private fun getRichestPlayerName(): String? {
        if (!MainConfig.moneyActivated) return null

        if (MoneyManager.tycoonPlayer.isEmpty()) {
            MoneyManager.refreshTycoon()
        }

        return MoneyManager.tycoonPlayer.entries.firstOrNull()?.key
    }

    private fun getBalance(player: Player): Double? {
        return PlayerData.moneyCache[player] ?: PlayerData.moneyCache[player.name]
    }

    private fun format(message: String, player: Player, balance: Double): String {
        return MoneyManager.replaceMoney(message, balance)
            .replace("%player%", player.name)
    }
}
