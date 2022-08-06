package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.data.dao.KitData
import github.gilbertokpl.essentialsk.data.dao.SpawnData
import github.gilbertokpl.essentialsk.data.dao.WarpData
import github.gilbertokpl.essentialsk.economy.EconomyHolder
import github.gilbertokpl.essentialsk.inventory.EditKitInventory
import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.EColor
import net.milkbowl.vault.economy.Economy
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit.getServer
import org.bukkit.event.Listener
import org.bukkit.plugin.ServicePriority
import java.io.InputStream
import java.net.URL
import java.util.*
import java.util.concurrent.CompletableFuture


internal object MainUtil {

    private const val METRICS_ID = 13_441

    val mainPath: String = EssentialsK.instance.dataFolder.path

    val langPath: String = EssentialsK.instance.dataFolder.path + "/lang/"

    val pluginPath: String = EssentialsK.instance.javaClass.protectionDomain.codeSource.location.path

    private val rand = Random()

    fun setupEconomy() {
        try {
            getServer().servicesManager.register(
                Economy::class.java,
                EconomyHolder(),
                EssentialsK.instance,
                ServicePriority.Highest
            )
        } catch (_: Exception) {
        }
    }

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
                KitData.loadKitCache()
            } catch (ex: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
            }
            try {
                WarpData.loadWarpCache()
            } catch (ex: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
            }
            try {
                SpawnData.loadSpawnCache()
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
            
            if (!c.commandData.active) {                
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
