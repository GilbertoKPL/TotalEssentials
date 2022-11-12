package github.gilbertokpl.total.util

import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import net.milkbowl.vault.economy.EconomyResponse
import java.text.DecimalFormat
import java.text.NumberFormat

object MoneyUtil {
    val tycoonPlayer = HashMap<String, Int>(10)
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

    fun withdrawPlayer(player: String, amount: Double): EconomyResponse {
        val money = PlayerData.moneyCache[player]
        if (money != null && amount < money) {
            PlayerData.moneyCache[player] = money - amount
            return EconomyResponse(amount, money - amount, EconomyResponse.ResponseType.SUCCESS, "")
        }
        return EconomyResponse(
            amount,
            money ?: 0.0,
            EconomyResponse.ResponseType.FAILURE,
            "player does not have a money"
        )
    }

    fun depositPlayer(player: String, amount: Double): EconomyResponse {
        val money = PlayerData.moneyCache[player]
        if (money != null) {
            PlayerData.moneyCache[player] = money + amount
            return EconomyResponse(amount, money + amount, EconomyResponse.ResponseType.SUCCESS, "")
        }
        return EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "player does not exist")
    }

    fun refreshTycoon() {
        if (MainConfig.moneyActivated) {
            tycoonPlayer.clear()
            var max = 0
            for (i in HashUtil.hashMapReversDouble(HashUtil.hashMapSortMapDouble(PlayerData.moneyCache.getMap()))) {
                if (max == 10) break
                max += 1
                tycoonPlayer[i.key] = i.value.toInt()
            }
        }
    }
}