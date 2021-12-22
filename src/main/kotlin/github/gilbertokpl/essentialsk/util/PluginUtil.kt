package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.commands.*
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.KitData
import github.gilbertokpl.essentialsk.data.SpawnData
import github.gilbertokpl.essentialsk.data.WarpData
import github.gilbertokpl.essentialsk.events.*
import github.gilbertokpl.essentialsk.inventory.EditKitInventory
import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.manager.IInstance
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.command.*
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.RegisteredListener
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Field
import java.net.URL
import java.net.URLClassLoader
import java.util.*
import java.util.concurrent.CompletableFuture


class PluginUtil {

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
        ReflectUtil.getInstance().getPlayers().forEach {
            it.sendMessage(message)
        }
    }

    fun startEvents() {
        startEventsHelper(
            listOf(
                ClickInventoryEvent(),
                CloseInventoryEvent(),
                PlayerAsyncChatEvent(),
                PlayerJoinEvent(),
                PlayerLeaveEvent(),
                PlayerPreCommandEvent(),
                PlayerDeathEvent(),
                PlayerTeleportEvent(),
                PlayerBedEnterEvent(),
                PlayerVehicleEnterEvent(),
                PlayerInteractEntityEvent(),
                CreatureSpawnEvent(),
                OpenInventoryEvent(),
                PortalCreateEvent(),
                PlayerPortalEvent(),
                EntityDamageEvent(),
                PlayerInteractEvent(),
                EntityDamageByEntityEvent(),
                EntityChangeBlockEvent(),
                BlockBurnEvent(),
                BlockIgniteEvent(),
                SignChangeEvent(),
                PlayerChangeWorldEvent(),
                ServerListPing(),
                PlayerRespawnEvent(),
                WeatherChangeEvent()
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
                KitData("").loadKitCache()
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
            try {
                WarpData("").loadWarpCache()
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
            try {
                SpawnData("").loadSpawnCache()
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
            return@supplyAsync true
        }, TaskUtil.getInstance().getExecutor()).get()
    }

    fun startInventories() {
        if (loadCache()) {
            if (MainConfig.getInstance().kitsActivated) {
                EditKitInventory.setup()
                KitGuiInventory.setup()
            }
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
            MainConfig.getInstance().kitsActivated
        )
        //nick
        startCommandsHelper(
            listOf(
                CommandNick()
            ),
            MainConfig.getInstance().nicksActivated
        )
        //homes
        startCommandsHelper(
            listOf(
                CommandSetHome(),
                CommandHome(),
                CommandDelHome()
            ),
            MainConfig.getInstance().homesActivated
        )
        //warps
        startCommandsHelper(
            listOf(
                CommandDelWarp(),
                CommandSetWarp(),
                CommandWarp()
            ),
            MainConfig.getInstance().warpsActivated
        )
        //tpa
        startCommandsHelper(
            listOf(
                CommandTpa(),
                CommandTpaccept(),
                CommandTpdeny()
            ),
            MainConfig.getInstance().tpaActivated
        )
        //tp
        startCommandsHelper(
            listOf(
                CommandTp()
            ),
            MainConfig.getInstance().tpActivated
        )
        //echest
        startCommandsHelper(
            listOf(
                CommandEchest()
            ),
            MainConfig.getInstance().echestActivated
        )
        //gamemode
        startCommandsHelper(
            listOf(
                CommandGamemode()
            ),
            MainConfig.getInstance().gamemodeActivated
        )
        //vanish
        startCommandsHelper(
            listOf(
                CommandVanish()
            ),
            MainConfig.getInstance().vanishActivated
        )
        //feed
        startCommandsHelper(
            listOf(
                CommandFeed()
            ),
            MainConfig.getInstance().feedActivated
        )
        //heal
        startCommandsHelper(
            listOf(
                CommandHeal()
            ),
            MainConfig.getInstance().healActivated
        )
        //light
        startCommandsHelper(
            listOf(
                CommandLight()
            ),
            MainConfig.getInstance().lightActivated
        )
        //back
        startCommandsHelper(
            listOf(
                CommandBack()
            ),
            MainConfig.getInstance().backActivated
        )
        //spawn
        startCommandsHelper(
            listOf(
                CommandSpawn(),
                CommandSetSpawn()
            ),
            MainConfig.getInstance().spawnActivated
        )
        //fly
        startCommandsHelper(
            listOf(
                CommandFly(),
            ),
            MainConfig.getInstance().flyActivated
        )
        //online
        startCommandsHelper(
            listOf(
                CommandOnline(),
            ),
            MainConfig.getInstance().onlineActivated
        )
        //announce
        startCommandsHelper(
            listOf(
                CommandAnnounce(),
            ),
            MainConfig.getInstance().announceActivated
        )
    }

    fun checkSpecialCaracteres(s: String?): Boolean {
        return s?.matches(Regex("[^A-Za-z0-9 ]")) ?: false
    }

    private fun startCommandsHelper(commands: List<CommandExecutor>, boolean: Boolean) {
        if (!boolean) return
        for (to in commands) {
            EssentialsK.instance.getCommand(
                to.javaClass.name.replace(
                    "github.gilbertokpl.essentialsk.commands.Command",
                    ""
                ).lowercase()
            )?.setExecutor(to)
        }
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
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
        } catch (ea: IllegalAccessException) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ea))
        }
        pluginManager.disablePlugin(plugin)
        if (plugins != null && plugins.contains(plugin)) plugins.remove(plugin)
        if (names != null && names.containsKey(name)) names.remove(name)
        if (listeners != null && reloadListeners) for (set in listeners.values) set.removeIf { value: RegisteredListener -> value.plugin === plugin }
        if (commandMap != null) {
            val it: MutableIterator<Map.Entry<String?, Command>> = commands!!.entries.iterator()
            while (it.hasNext()) {
                val (_, value) = it.next()
                if (value is PluginCommand) {
                    if (value.plugin === plugin) {
                        (commandMap as CommandMap?)?.let { it1 -> value.unregister(it1) }
                        it.remove()
                    }
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
        Metrics(EssentialsK.instance, 13441)
    }

    fun disableLoggers() {
        try {
            Class.forName("org.apache.logging.log4j.core.LoggerContext")
        } catch (e: ClassNotFoundException) {
            return
        }
        val ctx = LogManager.getContext(true) as LoggerContext
        val config = ctx.configuration
        val toRemove = ArrayList<String>()
        toRemove.add("com.zaxxer.hikari.pool.PoolBase")
        toRemove.add("com.zaxxer.hikari.pool.HikariPool")
        toRemove.add("com.zaxxer.hikari.HikariDataSource")
        toRemove.add("com.zaxxer.hikari.HikariConfig")
        toRemove.add("com.zaxxer.hikari.util.DriverDataSource")
        toRemove.add("Exposed")
        for (remove in toRemove) {
            config.getLoggerConfig(remove).level = Level.OFF
        }
    }

    companion object : IInstance<PluginUtil> {
        private val instance = createInstance()
        override fun createInstance(): PluginUtil = PluginUtil()
        override fun getInstance(): PluginUtil {
            return instance
        }
    }
}