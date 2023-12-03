package github.gilbertokpl.total.util

import github.gilbertokpl.total.cache.internal.inventory.EditKit
import github.gilbertokpl.total.cache.internal.inventory.Kit
import github.gilbertokpl.total.cache.internal.inventory.Playtime
import github.gilbertokpl.total.cache.internal.inventory.Shop
import github.gilbertokpl.total.cache.loop.AntiAfkLoop
import github.gilbertokpl.total.config.files.MainConfig
import java.util.*


internal object MainUtil {

    private val rand = Random()

    fun getRandom(list: List<String>): String {
        return list[rand.nextInt(list.size)]
    }

    fun consoleMessage(message: String) {
        println("${ColorUtil.CYAN.color}[${github.gilbertokpl.total.TotalEssentialsJava.instance.name}]${ColorUtil.RESET.color} $message")
    }

    fun serverMessage(message: String) {
        github.gilbertokpl.total.TotalEssentialsJava.basePlugin.getReflection().getPlayers().forEach {
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
            MoneyUtil.refreshTycoon()
        }
    }

    fun checkSpecialCharacters(s: String?): Boolean {
        return s?.matches(Regex("[^A-Za-z0-9 ]")) ?: false
    }
}
