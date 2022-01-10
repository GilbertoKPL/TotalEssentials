package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.commands.*
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.start.KitDataLoader
import github.gilbertokpl.essentialsk.data.start.LocationsLoader
import github.gilbertokpl.essentialsk.inventory.EditKitInventory
import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import github.gilbertokpl.essentialsk.listeners.*
import github.gilbertokpl.essentialsk.manager.EColor
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.command.*
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.RegisteredListener
import java.awt.Color
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Field
import java.net.URL
import java.net.URLClassLoader
import java.util.*
import java.util.concurrent.CompletableFuture

object PluginUtil {

    private const val METRICS_ID = 13_441

    val mainPath: String = EssentialsK.instance.dataFolder.path

    val langPath: String = EssentialsK.instance.dataFolder.path + "/lang/"

    val pluginPath: String = EssentialsK.instance.javaClass.protectionDomain.codeSource.location.path

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
        startEventsHelper(
            listOf(
                InventoryClick(),
                InventoryClose(),
                ChatEventAsync(),
                PlayerJoin(),
                PlayerLeave(),
                PlayerPreCommand(),
                PlayerDeath(),
                PlayerTeleport(),
                PlayerBedEnter(),
                EntityVehicleEnter(),
                PlayerInteractEntity(),
                CreatureSpawn(),
                InventoryOpen(),
                EntityPortalCreate(),
                PlayerPortal(),
                EntityDamage(),
                PlayerInteract(),
                EntityDamageEntity(),
                EntityChangeBlock(),
                BurnEvent(),
                IgniteEvent(),
                EntitySignChange(),
                PlayerChangeWorld(),
                ServerListPing(),
                PlayerRespawn(),
                WeatherChange()
            )
        )
    }

    private fun startEventsHelper(event: List<Listener>) {
        event.forEach {
            EssentialsK.instance.server.pluginManager.registerEvents(it, EssentialsK.instance)
        }
    }

    private fun loadCache(): Boolean {
        return CompletableFuture.supplyAsync({
            try {
                KitDataLoader.loadKitCache()
            } catch (ex: Exception) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
            }
            try {
                LocationsLoader.loadWarpCache()
            } catch (ex: Exception) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
            }
            try {
                LocationsLoader.loadSpawnCache()
            } catch (ex: Exception) {
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
        startCommandsHelper(
            listOf(
                CommandEssentialsK()
            ),
            true
        )
        //kits
        startCommandsHelper(
            listOf(
                CommandCreateKit(),
                CommandDelKit(),
                CommandEditKit(),
                CommandKit(),
                CommandGiveKit()
            ),
            MainConfig.kitsActivated
        )
        //nick
        startCommandsHelper(
            listOf(
                CommandNick()
            ),
            MainConfig.nicksActivated
        )
        //homes
        startCommandsHelper(
            listOf(
                CommandSetHome(),
                CommandHome(),
                CommandDelHome()
            ),
            MainConfig.homesActivated
        )
        //warps
        startCommandsHelper(
            listOf(
                CommandDelWarp(),
                CommandSetWarp(),
                CommandWarp()
            ),
            MainConfig.warpsActivated
        )
        //tpa
        startCommandsHelper(
            listOf(
                CommandTpa(),
                CommandTpaccept(),
                CommandTpdeny()
            ),
            MainConfig.tpaActivated
        )
        //tp
        startCommandsHelper(
            listOf(
                CommandTp()
            ),
            MainConfig.tpActivated
        )
        //tphere
        startCommandsHelper(
            listOf(
                CommandTphere()
            ),
            MainConfig.tphereActivated
        )
        //echest
        startCommandsHelper(
            listOf(
                CommandEchest()
            ),
            MainConfig.echestActivated
        )
        //gamemode
        startCommandsHelper(
            listOf(
                CommandGamemode()
            ),
            MainConfig.gamemodeActivated
        )
        //vanish
        startCommandsHelper(
            listOf(
                CommandVanish()
            ),
            MainConfig.vanishActivated
        )
        //feed
        startCommandsHelper(
            listOf(
                CommandFeed()
            ),
            MainConfig.feedActivated
        )
        //heal
        startCommandsHelper(
            listOf(
                CommandHeal()
            ),
            MainConfig.healActivated
        )
        //light
        startCommandsHelper(
            listOf(
                CommandLight()
            ),
            MainConfig.lightActivated
        )
        //back
        startCommandsHelper(
            listOf(
                CommandBack()
            ),
            MainConfig.backActivated
        )
        //spawn
        startCommandsHelper(
            listOf(
                CommandSpawn(),
                CommandSetSpawn()
            ),
            MainConfig.spawnActivated
        )
        //fly
        startCommandsHelper(
            listOf(
                CommandFly(),
            ),
            MainConfig.flyActivated
        )
        //online
        startCommandsHelper(
            listOf(
                CommandOnline(),
            ),
            MainConfig.onlineActivated
        )
        //announce
        startCommandsHelper(
            listOf(
                CommandAnnounce(),
            ),
            MainConfig.announceActivated
        )
        //craft
        startCommandsHelper(
            listOf(
                CommandCraft(),
            ),
            MainConfig.craftActivated
        )
        //trash
        startCommandsHelper(
            listOf(
                CommandTrash(),
            ),
            MainConfig.trashActivated
        )
        //speed
        startCommandsHelper(
            listOf(
                CommandSpeed(),
            ),
            MainConfig.speedActivated
        )
    }

    private fun startCommandsHelper(commands: List<CommandExecutor>, boolean: Boolean) {
        for (to in commands) {
            val cmdName = to.javaClass.name.replace(
                "github.gilbertokpl.essentialsk.commands.Command",
                ""
            ).lowercase()
            if (!boolean) {
                ReflectUtil.removeCommand(cmdName)
                return
            }
            EssentialsK.instance.getCommand(
                cmdName
            )?.setExecutor(to)

        }
    }

    fun randomColor(): Color = Color.getHSBColor(
        (Math.random() * 255 + 1).toFloat(),
        (Math.random() * 255 + 1).toFloat(),
        (Math.random() * 255 + 1).toFloat()
    )

    fun checkSpecialCaracteres(s: String?): Boolean {
        return s?.matches(Regex("[^A-Za-z0-9 ]")) ?: false
    }

    fun unloadPlugin(plugin: Plugin) {
        val name: String = plugin.name
        val pluginManager: PluginManager = Bukkit.getPluginManager()
        var commandMap: SimpleCommandMap? = null
        var plugins: MutableList<Plugin?>? = null
        var names: MutableMap<String?, Plugin?>? = null
        var commands: MutableMap<String?, Command>? = null
        var listeners: Map<Event?, SortedSet<RegisteredListener>>? = null
        var reloadListeners = true
        pluginManager.disablePlugin(plugin)
        try {
            val pluginsField: Field = Bukkit.getPluginManager().javaClass.getDeclaredField("plugins")
            pluginsField.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            plugins = pluginsField.get(pluginManager) as MutableList<Plugin?>?
            val lookupNamesField: Field = Bukkit.getPluginManager().javaClass.getDeclaredField("lookupNames")
            lookupNamesField.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            names = lookupNamesField.get(pluginManager) as MutableMap<String?, Plugin?>?
            try {
                val listenersField: Field = Bukkit.getPluginManager().javaClass.getDeclaredField("listeners")
                listenersField.isAccessible = true
                @Suppress("UNCHECKED_CAST")
                listeners = listenersField.get(pluginManager) as Map<Event?, SortedSet<RegisteredListener>>?
            } catch (e: Exception) {
                reloadListeners = false
            }
            val commandMapField: Field = Bukkit.getPluginManager().javaClass.getDeclaredField("commandMap")
            commandMapField.isAccessible = true
            commandMap = commandMapField.get(pluginManager) as SimpleCommandMap?
            val knownCommandsField: Field = SimpleCommandMap::class.java.getDeclaredField("knownCommands")
            knownCommandsField.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            commands = knownCommandsField.get(commandMap) as MutableMap<String?, Command>?
        } catch (e: NoSuchFieldException) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
        } catch (ea: IllegalAccessException) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ea))
        }
        pluginManager.disablePlugin(plugin)
        if (plugins != null && plugins.contains(plugin)) plugins.remove(plugin)
        if (names != null && names.containsKey(name)) names.remove(name)
        if (listeners != null && reloadListeners) {
            for (set in listeners.values) {
                set.removeIf { value: RegisteredListener -> value.plugin === plugin }
            }
        }
        if (commandMap != null) {
            val it: MutableIterator<Map.Entry<String?, Command>> = commands!!.entries.iterator()
            while (it.hasNext()) {
                val (_, value) = it.next()
                if (value is PluginCommand && value.plugin === plugin) {
                    (commandMap as CommandMap?)?.let { it1 -> value.unregister(it1) }
                    it.remove()
                }
            }
        }
        val cl: ClassLoader = plugin.javaClass.classLoader
        if (cl is URLClassLoader) try {
            cl.close()
        } catch (ioe: IOException) {
            ExceptionUtils.getStackTrace(ioe)
        }
    }

    fun startMetrics() {
        Metrics(EssentialsK.instance, METRICS_ID)
    }
}
