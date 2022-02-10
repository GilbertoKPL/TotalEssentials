package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.config.files.LangConfig

object MoneyUtil {

    private fun getUnityMoney(double: Double) : String {
        return if (double <= 1.0) {
            LangConfig.moneySingular
        } else {
            LangConfig.moneyPlural
        }
    }

    fun coinReplacer(string: String, money: Double) : String {
        return string
            .replace("%money%", money.toString())
            .replace("%unity%", getUnityMoney(money))
    }
}