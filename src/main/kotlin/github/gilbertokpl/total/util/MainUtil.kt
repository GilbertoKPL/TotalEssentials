package github.gilbertokpl.total.util

import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.internal.EditKitInventory
import github.gilbertokpl.total.cache.internal.KitGuiInventory
import github.gilbertokpl.total.cache.internal.ShopInventory
import github.gilbertokpl.total.cache.loop.AntiAfkLoop
import java.io.InputStream
import java.net.URL
import java.util.*


internal object MainUtil {

    private val rand = Random()

    fun getRandom(list: List<String>): String {
        return list[rand.nextInt(list.size)]
    }

    fun fileDownloader(url: String): InputStream? {
        val stream = URL(url).openConnection()
        stream.connect()
        return stream.getInputStream()
    }

    fun consoleMessage(message: String) {
        println("${ColorUtil.CYAN.color}[${github.gilbertokpl.total.TotalEssentials.instance.name}]${ColorUtil.RESET.color} $message")
    }

    fun serverMessage(message: String) {
        github.gilbertokpl.total.TotalEssentials.basePlugin.getReflection().getPlayers().forEach {
            it.sendMessage(message)
        }
    }

    fun startInventories() {
        if (MainConfig.kitsActivated) {
            EditKitInventory.setup()
            KitGuiInventory.setup()
        }
        if (MainConfig.shopEnabled) {
            ShopInventory.setup()
        }
        if (MainConfig.antiafkEnabled) {
            AntiAfkLoop.start()
        }
    }

    fun checkSpecialCharacters(s: String?): Boolean {
        return s?.matches(Regex("[^A-Za-z0-9 ]")) ?: false
    }
}
