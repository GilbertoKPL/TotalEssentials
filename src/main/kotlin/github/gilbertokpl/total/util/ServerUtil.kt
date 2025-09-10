package github.gilbertokpl.total.util

import github.gilbertokpl.core.utils.ConsoleColorUtil
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.inventory.EditKit
import github.gilbertokpl.total.cache.inventory.Kit
import github.gilbertokpl.total.cache.inventory.Playtime
import github.gilbertokpl.total.cache.inventory.Shop
import github.gilbertokpl.total.cache.loop.AntiAfkLoop
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.economy.MoneyManager
import java.util.*


internal object ServerUtil {

    private val rand = Random()

    fun getRandom(list: List<String>): String {
        return list[rand.nextInt(list.size)]
    }

    fun consoleMessage(message: String) {
        println("${ConsoleColorUtil.CYAN.color}[${TotalEssentials.getInstance().name}]${ConsoleColorUtil.RESET.color} $message")
    }

    fun serverMessage(message: String) {
        TotalEssentials.getCore().getReflection().getPlayers().forEach {
            it.sendMessage(message)
        }
    }

    fun startInventories() {
        if (MainConfig.kitsActivated) {
            EditKit.setup()
            Kit.setup()
        }
        if (MainConfig.shopActivated) {
            Shop.setup()
        }
        if (MainConfig.antiafkEnabled) {
            AntiAfkLoop.start()
        }
        if (MainConfig.playtimeActivated) {
            Playtime.setup()
        }
        if (MainConfig.moneyActivated) {
            MoneyManager.refreshTycoon()
        }
    }

    fun checkSpecialCharacters(s: String?): Boolean {
        return s?.matches(Regex("[^A-Za-z0-9 ]")) ?: false
    }
}
