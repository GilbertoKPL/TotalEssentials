package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.player.modify.MoneyCache.addMoney
import github.gilbertokpl.essentialsk.player.modify.MoneyCache.takeMoney
import net.milkbowl.vault.economy.EconomyResponse
import java.text.DecimalFormat
import java.text.NumberFormat

object MoneyUtil {

    val tycoonPlayer = LinkedHashMap<String, Int>(10)
    private val format: NumberFormat = DecimalFormat("#,##0.00")

    private fun getUnityMoney(double: Double): String {
        return if (double <= 1.0) {
            LangConfig.moneySingular
        } else {
            LangConfig.moneyPlural
        }
    }

    fun coinReplacer(money: Double): String {
        return format.format(money)
    }

    fun coinReplacer(string: String, money: Double): String {
        return string
            .replace("%money%", format.format(money))
            .replace("%unity%", getUnityMoney(money))
    }

    fun withdrawPlayer(playerData: PlayerData?, amount: Double): EconomyResponse {
        if (playerData != null && amount < playerData.moneyCache) {
            playerData.takeMoney(amount)
            return EconomyResponse(amount, playerData.moneyCache, EconomyResponse.ResponseType.SUCCESS, "")
        }
        return EconomyResponse(
            amount,
            playerData?.moneyCache ?: 0.0,
            EconomyResponse.ResponseType.FAILURE,
            "player does not have a money"
        )
    }

    fun depositPlayer(playerData: PlayerData?, amount: Double): EconomyResponse {
        if (playerData != null) {
            playerData.addMoney(amount)
            return EconomyResponse(amount, playerData.moneyCache, EconomyResponse.ResponseType.SUCCESS, "")
        }
        return EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "player does not exist")
    }

    fun refreashTycoon() {
        if (MainConfig.moneyActivated) {
            tycoonPlayer.clear()
            var max = 0
            for (i in HashUtil.hashMapReverse(HashUtil.hashMapSortMap(PlayerData.moneyMap()))) {
                if (max == 10) break
                max += 1
                tycoonPlayer[i.key] = i.value
            }
        }
    }

}