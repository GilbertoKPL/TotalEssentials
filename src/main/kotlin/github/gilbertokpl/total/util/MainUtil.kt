package github.gilbertokpl.total.util

import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.internal.EditKitInventory
import github.gilbertokpl.total.cache.internal.KitGuiInventory
import java.io.InputStream
import java.net.URL
import java.util.*


internal object MainUtil {

    private const val METRICS_ID = 13_441

    val mainPath: String = github.gilbertokpl.total.TotalEssentials.instance.dataFolder.path

    val langPath: String = github.gilbertokpl.total.TotalEssentials.instance.dataFolder.path + "/lang/"

    val pluginPath: String =
        github.gilbertokpl.total.TotalEssentials.instance.javaClass.protectionDomain.codeSource.location.path

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
    }

    fun checkSpecialCaracteres(s: String?): Boolean {
        return s?.matches(Regex("[^A-Za-z0-9 ]")) ?: false
    }
}
