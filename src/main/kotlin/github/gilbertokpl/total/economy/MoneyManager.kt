package github.gilbertokpl.total.economy

import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import net.milkbowl.vault.economy.EconomyResponse
import java.text.DecimalFormat
import java.text.NumberFormat

object MoneyManager {

    private const val TOP_PLAYERS_LIMIT = 10
    private const val THOUSAND = 1000L

    val tycoonPlayer = LinkedHashMap<String, Double>(TOP_PLAYERS_LIMIT)

    private val numberFormatter: NumberFormat = DecimalFormat("#,##0.00")

    /**
     * Formata um valor monetário de acordo com a configuração
     * @param amount Valor a ser formatado
     * @return String formatada do valor
     */
    fun formatMoney(amount: Double): String {
        return if (MainConfig.moneyExtended) {
            formatNumberInWords(amount)
        } else {
            numberFormatter.format(amount)
        }
    }

    /**
     * Substitui placeholders de dinheiro em uma string
     * @param text Texto com placeholders
     * @param amount Valor monetário
     * @return Texto com placeholders substituídos
     */
    fun replaceMoney(text: String, amount: Double): String {
        return text
            .replace("%money%", formatMoney(amount))
            .replace("%unity%", LangConfig.moneySymbol)
    }

    /**
     * Remove dinheiro de um jogador
     * @param playerName Nome do jogador
     * @param amount Quantia a ser removida
     * @return Resposta da transação
     */
    fun withdrawPlayer(playerName: String, amount: Double): EconomyResponse {
        val currentBalance = PlayerData.moneyCache[playerName]

        if (currentBalance == null) {
            return createFailureResponse(
                amount = amount,
                balance = 0.0,
                error = "Player does not exist"
            )
        }

        if (amount > currentBalance) {
            return createFailureResponse(
                amount = amount,
                balance = currentBalance,
                error = "Insufficient funds"
            )
        }

        val newBalance = currentBalance - amount
        PlayerData.moneyCache[playerName] = newBalance

        return createSuccessResponse(amount, newBalance)
    }

    /**
     * Adiciona dinheiro a um jogador
     * @param playerName Nome do jogador
     * @param amount Quantia a ser adicionada
     * @return Resposta da transação
     */
    fun depositPlayer(playerName: String, amount: Double): EconomyResponse {
        val currentBalance = PlayerData.moneyCache[playerName]

        if (currentBalance == null) {
            return createFailureResponse(
                amount = amount,
                balance = 0.0,
                error = "Player does not exist"
            )
        }

        val newBalance = currentBalance + amount
        PlayerData.moneyCache[playerName] = newBalance

        return createSuccessResponse(amount, newBalance)
    }

    /**
     * Atualiza o ranking dos jogadores mais ricos
     */
    fun refreshTycoon() {
        if (!MainConfig.moneyActivated) return

        tycoonPlayer.clear()

        val topPlayers = PlayerData.moneyCache.getMap()
            .filterValues { it != null }
            .mapValues { it.value!! }
            .entries
            .sortedByDescending { it.value }
            .take(TOP_PLAYERS_LIMIT)

        topPlayers.forEach { (playerName, balance) ->
            tycoonPlayer[playerName] = balance
        }
    }

    /**
     * Formata um número grande em palavras (português)
     * Exemplo: 1500000 → "1 milhão e 500 mil"
     */
    fun formatNumberInWords(number: Double): String {
        val value = number.toLong()

        if (value == 0L) {
            return "0 ${LangConfig.moneySuffixPlural}"
        }

        val parts = buildNumberParts(value)

        if (parts.isEmpty()) {
            return "0 ${LangConfig.moneySuffixPlural}"
        }

        return formatParts(parts)
    }

    private fun buildNumberParts(value: Long): List<String> {
        val parts = mutableListOf<String>()
        var remainingValue = value
        var magnitudeIndex = 0

        while (remainingValue > 0 && magnitudeIndex < NumberMagnitude.values().size) {
            val segmentValue = (remainingValue % THOUSAND).toInt()

            if (segmentValue > 0) {
                val magnitude = NumberMagnitude.values()[magnitudeIndex]
                val suffix = getSuffix(magnitude, segmentValue)
                parts.add(0, "$segmentValue $suffix")
            }

            remainingValue /= THOUSAND
            magnitudeIndex++
        }

        return parts
    }

    private fun getSuffix(magnitude: NumberMagnitude, value: Int): String {
        val isPlural = value > 1

        return when (magnitude) {
            NumberMagnitude.UNITS -> if (isPlural) LangConfig.moneySuffixPlural else LangConfig.moneySuffixSingular
            NumberMagnitude.THOUSAND -> "mil"
            NumberMagnitude.MILLION -> if (isPlural) "milhões" else "milhão"
            NumberMagnitude.BILLION -> if (isPlural) "bilhões" else "bilhão"
            NumberMagnitude.TRILLION -> if (isPlural) "trilhões" else "trilhão"
            NumberMagnitude.QUADRILLION -> if (isPlural) "quadrilhões" else "quadrilhão"
            NumberMagnitude.QUINTILLION -> if (isPlural) "quintilhões" else "quintilhão"
            NumberMagnitude.SEXTILLION -> if (isPlural) "sextilhões" else "sextilhão"
            NumberMagnitude.SEPTILLION -> if (isPlural) "setilhões" else "setilhão"
            NumberMagnitude.OCTILLION -> if (isPlural) "octilhões" else "octilhão"
        }
    }

    private fun formatParts(parts: List<String>): String {
        if (parts.size == 1) {
            return parts[0]
        }

        val partsWithConnector = parts.toMutableList()
        val lastIndex = partsWithConnector.lastIndex
        partsWithConnector[lastIndex] = "e ${partsWithConnector[lastIndex]}"

        return partsWithConnector.joinToString(", ")
    }

    private fun createSuccessResponse(amount: Double, newBalance: Double): EconomyResponse {
        return EconomyResponse(
            amount,
            newBalance,
            EconomyResponse.ResponseType.SUCCESS,
            ""
        )
    }

    private fun createFailureResponse(amount: Double, balance: Double, error: String): EconomyResponse {
        return EconomyResponse(
            amount,
            balance,
            EconomyResponse.ResponseType.FAILURE,
            error
        )
    }

    /**
     * Enum para magnitudes de números
     */
    private enum class NumberMagnitude {
        UNITS,          // 0
        THOUSAND,       // 1
        MILLION,        // 2
        BILLION,        // 3
        TRILLION,       // 4
        QUADRILLION,    // 5
        QUINTILLION,    // 6
        SEXTILLION,     // 7
        SEPTILLION,     // 8
        OCTILLION       // 9
    }
}