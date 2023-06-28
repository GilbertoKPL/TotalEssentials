package github.gilbertokpl.total.util

import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import net.milkbowl.vault.economy.EconomyResponse
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat

object MoneyUtil {
    val tycoonPlayer = LinkedHashMap<String, Double>(10)
    private val format: NumberFormat = DecimalFormat("#,##0.00")

    fun coinReplacer(money: Double): String {
        return if (MainConfig.moneyExtended) {
                formatNumberInWords(money)
            }
            else {
                format.format(money)
            }
    }


    fun coinReplacer(string: String, money: Double): String {
        return string
            .replace("%money%", coinReplacer(money))
            .replace("%unity%", LangConfig.moneySymbol)
    }

    fun withdrawPlayer(player: String, amount: Double): EconomyResponse {
        val money = PlayerData.moneyCache[player]
        if ((money != null) && (amount <= money)) {
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

            val newHash = LinkedHashMap<String, Double>()

            PlayerData.moneyCache.getMap().forEach {
                val value = it.value
                if (value != null) {
                    newHash[it.key] = value
                }
            }

            for (i in newHash.entries.sortedBy { it.value }.associate { it.toPair() }.asIterable().reversed()) {
                if (max == 10) break
                max += 1
                tycoonPlayer[i.key] = i.value
            }
        }
    }

    fun formatNumberInWords(number: Double): String {
        val suffixes = listOf("", "mil", "milhão", "bilhão", "trilhão", "quadrilhão", "quintilhão", "sextilhão", "setilhão", "octilhão")
        val pluralSuffixes = listOf("", "mil", "milhões", "bilhões", "trilhões", "quadrilhões", "quintilhões", "sextilhões", "setilhões", "octilhões")
        val value = number.toLong()
        val parts = mutableListOf<String>()

        var remainingValue = value
        var suffixIndex = 0

        while (remainingValue > 0) {
            val part = (remainingValue % 1000).toInt()
            if (part > 0) {
                val suffix = if (part > 1) pluralSuffixes[suffixIndex] else suffixes[suffixIndex]
                parts.add(0, "$part $suffix")
            }

            remainingValue /= 1000
            suffixIndex++
        }

        if (parts.size > 1) {
            val lastIndex = parts.lastIndex
            parts[lastIndex] = "e " + parts[lastIndex]
        }

        return parts.joinToString(", ")
    }
}