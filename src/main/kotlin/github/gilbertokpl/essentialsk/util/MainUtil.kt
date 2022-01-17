package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.objects.KitDataV2
import github.gilbertokpl.essentialsk.data.objects.SpawnDataV2
import github.gilbertokpl.essentialsk.data.objects.WarpDataV2
import github.gilbertokpl.essentialsk.inventory.EditKitInventory
import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.EColor
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bstats.bukkit.Metrics
import org.bukkit.event.Listener
import java.io.InputStream
import java.net.URL
import java.util.*
import java.util.concurrent.CompletableFuture

object MainUtil {

    private const val METRICS_ID = 13_441

    val mainPath: String = EssentialsK.instance.dataFolder.path

    val langPath: String = EssentialsK.instance.dataFolder.path + "/lang/"

    val pluginPath: String = EssentialsK.instance.javaClass.protectionDomain.codeSource.location.path

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
        println("${EColor.CYAN.color}[${EssentialsK.instance.name}]${EColor.RESET.color} $message")
    }

    fun serverMessage(message: String) {
        ReflectUtil.getPlayers().forEach {
            it.sendMessage(message)
        }
    }

    fun startEvents() {
        val classes = ReflectUtil.getClasses<Listener>("github.gilbertokpl.essentialsk.listeners.")
        classes.forEach {
            EssentialsK.instance.server.pluginManager.registerEvents(it, EssentialsK.instance)
        }
    }

    private fun loadCache(): Boolean {
        return CompletableFuture.supplyAsync({
            try {
                KitDataV2.loadKitCache()
            } catch (ex: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
            }
            try {
                WarpDataV2.loadWarpCache()
            } catch (ex: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
            }
            try {
                SpawnDataV2.loadSpawnCache()
            } catch (ex: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
            }
            return@supplyAsync true
        }, TaskUtil.getExecutor()).get()
    }

    fun startInventories() {
        if (loadCache() && MainConfig.kitsActivated) {
            EditKitInventory.setup()
            KitGuiInventory.setup()
        }
    }

    fun startCommands() {

        val classes = ReflectUtil.getClasses<CommandCreator>("github.gilbertokpl.essentialsk.commands.")

        for (c in classes) {
            val cmdName = c.javaClass.name.replace(
                "github.gilbertokpl.essentialsk.commands.Command",
                ""
            ).lowercase()
            if (!c.active) {
                ReflectUtil.removeCommand(cmdName)
                continue
            }
            EssentialsK.instance.getCommand(
                cmdName
            )?.setExecutor(c)
        }
    }

    fun checkSpecialCaracteres(s: String?): Boolean {
        return s?.matches(Regex("[^A-Za-z0-9 ]")) ?: false
    }

    fun startMetrics() {
        Metrics(EssentialsK.instance, METRICS_ID)
    }
}
