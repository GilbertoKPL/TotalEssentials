package github.genesyspl.cardinal.util

import github.genesyspl.cardinal.Cardinal
import github.genesyspl.cardinal.commands.*
import github.genesyspl.cardinal.configs.MainConfig
import github.genesyspl.cardinal.data.start.KitDataLoader
import github.genesyspl.cardinal.data.start.LocationsLoader
import github.genesyspl.cardinal.events.*
import github.genesyspl.cardinal.inventory.EditKitInventory
import github.genesyspl.cardinal.inventory.KitGuiInventory
import github.genesyspl.cardinal.manager.EColor
import github.genesyspl.cardinal.manager.IInstance
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


class PluginUtil {

    val mainPath: String = Cardinal.instance.dataFolder.path

    val langPath: String = Cardinal.instance.dataFolder.path + "/lang/"

    val pluginPath: String =
        Cardinal.instance.javaClass.protectionDomain.codeSource.location.path

    fun fileDownloader(url: String): InputStream? {
        val stream = URL(url).openConnection()
        stream.connect()
        return stream.getInputStream()
    }

    fun consoleMessage(message: String) {
        println("${EColor.CYAN.color}[${Cardinal.instance.name}]${EColor.RESET.color} $message")
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
            Cardinal.instance.server.pluginManager.registerEvents(
                it,
                Cardinal.instance
            )
        }
    }

    private fun loadCache(): Boolean {
        return CompletableFuture.supplyAsync({
            try {
                KitDataLoader.getInstance().loadKitCache()
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
            try {
                LocationsLoader.getInstance().loadWarpCache()
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
            try {
                LocationsLoader.getInstance().loadSpawnCache()
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
                CommandCardinal()
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
        //tphere
        startCommandsHelper(
            listOf(
                CommandTphere()
            ),
            MainConfig.getInstance().tphereActivated
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
        //craft
        startCommandsHelper(
            listOf(
                CommandCraft(),
            ),
            MainConfig.getInstance().craftActivated
        )
        //trash
        startCommandsHelper(
            listOf(
                CommandTrash(),
            ),
            MainConfig.getInstance().trashActivated
        )
        //speed
        startCommandsHelper(
            listOf(
                CommandSpeed(),
            ),
            MainConfig.getInstance().speedActivated
        )
    }

    private fun startCommandsHelper(commands: List<CommandExecutor>, boolean: Boolean) {
        for (to in commands) {
            val cmdName = to.javaClass.name.replace(
                "github.genesyspl.cardinal.commands.Command",
                ""
            ).lowercase()
            if (!boolean) {
                ReflectUtil.getInstance().removeCommand(cmdName)
                return
            }
            Cardinal.instance.getCommand(
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
        Metrics(Cardinal.instance, 13865)
    }

    companion object : IInstance<PluginUtil> {
        private val instance = createInstance()
        override fun createInstance(): PluginUtil = PluginUtil()
        override fun getInstance(): PluginUtil {
            return instance
        }
    }
}