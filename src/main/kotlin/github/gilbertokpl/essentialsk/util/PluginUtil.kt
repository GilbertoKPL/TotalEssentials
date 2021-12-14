package github.gilbertokpl.essentialsk.util

import com.google.gson.JsonParser
import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.commands.*
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.data.KitData
import github.gilbertokpl.essentialsk.data.WarpData
import github.gilbertokpl.essentialsk.events.*
import github.gilbertokpl.essentialsk.inventory.EditKitInventory
import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.manager.IInstance
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.command.*
import org.bukkit.entity.Player
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
import java.util.concurrent.TimeUnit


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

    fun getPlayerUUID(p: Player): String {
        return if (EssentialsK.instance.server.onlineMode) {
            p.uniqueId.toString()
        } else {
            p.name.lowercase()
        }
    }

    fun getPlayerUUID(p: OfflinePlayer): String {
        return if (EssentialsK.instance.server.onlineMode) {
            val jsonPlayer = JsonParser.parseString(
                IOUtils.toString(
                    URL("https://api.mojang.com/users/profiles/minecraft/${p.name!!.lowercase()}"),
                    "UTF-8"
                )
            ).asJsonObject
            jsonPlayer.get("id").asString
        } else {
            p.name!!.lowercase()
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
                PlayerPreCommandEvent()
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
    }

    fun colorPermission(p: Player, message: String): String {
        if (!message.contains("&")) return message
        var newMessage = message
        fun colorHelper(color: String) {
            if (p.hasPermission("essentialsk.color.$color")) {
                newMessage = newMessage.replace(color, color.replace("&", "§"))
            }
        }
        listOf("&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f").forEach {
            colorHelper(it)
        }
        return newMessage
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

    fun getNumberPermission(player: Player, permission: String, default: Int): Int {
        for (perm in player.effectivePermissions) {
            val permString = perm.permission
            if (permString.startsWith(permission)) {
                val amount = permString.split(".").toTypedArray()
                return try {
                    amount.last().toInt()
                } catch (e: Exception) {
                    default
                }
            }
        }
        return default
    }


    fun convertStringToMillis(timeString: String): Long {
        val messageSplit = timeString.split(" ")
        var convert = 0L
        for (i in messageSplit) {
            val split = i.replace("(?<=[A-Z])(?=[A-Z])|(?<=[a-z])(?=[A-Z])|(?<=\\D)$".toRegex(), "1")
                .split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)".toRegex())
            val unit = try {
                split[1]
            } catch (e: Exception) {
                null
            }
            convert += if (unit == null) {
                try {
                    TimeUnit.MINUTES.toMillis(split[0].toLong())
                } catch (e: Exception) {
                    0L
                }
            } else {
                try {
                    when (unit.lowercase()) {
                        "s" -> TimeUnit.SECONDS.toMillis(split[0].toLong())
                        "m" -> TimeUnit.MINUTES.toMillis(split[0].toLong())
                        "h" -> TimeUnit.HOURS.toMillis(split[0].toLong())
                        "d" -> TimeUnit.DAYS.toMillis(split[0].toLong())
                        else -> TimeUnit.MINUTES.toMillis(split[0].toLong())
                    }
                } catch (e: Exception) {
                    0L
                }
            }
        }
        return convert
    }


    fun convertMillisToString(time: Long, short: Boolean): String {
        val toSend = ArrayList<String>()
        fun helper(time: Long, sendShort: String, send: String) {
            if (time > 0L) {
                if (short) {
                    toSend.add(sendShort)
                } else {
                    toSend.add(send)
                }
            }
        }

        var seconds = time / 1000
        var minutes = seconds / 60
        var hours = minutes / 60
        val days = hours / 24
        seconds %= 60
        minutes %= 60
        hours %= 24
        val uniDays = if (days < 2) {
            GeneralLang.getInstance().timeDay
        } else GeneralLang.getInstance().timeDays
        helper(days, "$days ${GeneralLang.getInstance().timeDayShort}", "$days $uniDays")
        val uniHours = if (hours < 2) {
            GeneralLang.getInstance().timeHour
        } else GeneralLang.getInstance().timeHours
        helper(hours, "$hours ${GeneralLang.getInstance().timeHourShort}", "${hours % 24} $uniHours")
        val uniMinutes = if (minutes < 2) {
            GeneralLang.getInstance().timeMinute
        } else GeneralLang.getInstance().timeMinutes
        helper(minutes, "$minutes ${GeneralLang.getInstance().timeMinuteShort}", "${minutes % 60} $uniMinutes")
        val uniSeconds = if (seconds < 2) {
            GeneralLang.getInstance().timeSecond
        } else GeneralLang.getInstance().timeSeconds
        helper(seconds, "$seconds ${GeneralLang.getInstance().timeSecondShort}", "${seconds % 60} $uniSeconds")
        var toReturn = ""
        var quaint = 0
        for (values in toSend) {
            quaint += 1
            toReturn = if (quaint == values.length) {
                if (toReturn == "") {
                    "$values."
                } else {
                    "$toReturn, $values."
                }
            } else {
                if (toReturn == "") {
                    values
                } else {
                    "$toReturn, $values"
                }
            }
        }
        return toReturn
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

    fun startMaterials() {
        Dao.getInstance().material["glass"] =
            materialHelper(listOf("STAINED_GLASS_PANE", "THIN_GLASS", "YELLOW_STAINED_GLASS"))
        Dao.getInstance().material["clock"] = materialHelper(listOf("CLOCK", "WATCH"))
        Dao.getInstance().material["feather"] = materialHelper(listOf("FEATHER"))
    }

    private fun materialHelper(material: List<String>): Material {
        var mat = Material.AIR
        for (i in material) {
            val toPut = Material.getMaterial(i)
            if (toPut != null) {
                mat = toPut
                break
            }
        }
        return mat
    }

    fun disableLoggers() {
        try {
            Class.forName("org.apache.logging.log4j.core.LoggerContext")
        } catch (e: ClassNotFoundException) {
            return
        }
        val ctx = LogManager.getContext(false) as LoggerContext
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