package github.gilbertokpl.total.util

import github.gilbertokpl.total.config.files.LangConfig
import org.bukkit.Bukkit
import org.bukkit.command.*
import org.bukkit.event.Event
import org.bukkit.plugin.*
import java.io.File
import java.io.IOException
import java.lang.reflect.Field
import java.net.URLClassLoader
import java.util.*

internal object PluginUtil {

    fun getPluginByName(name: String): Plugin? {
        for (plugin in Bukkit.getPluginManager().plugins) {
            if (name.equals(plugin.name, ignoreCase = true)) return plugin
        }
        return null
    }

    fun enable(plugin: Plugin?) {
        if (plugin != null && !plugin.isEnabled) Bukkit.getPluginManager().enablePlugin(plugin)
    }

    fun disable(plugin: Plugin?) {
        if (plugin != null && plugin.isEnabled) Bukkit.getPluginManager().disablePlugin(plugin)
    }

    private fun load(plugin: Plugin): String {
        return load(plugin.name)
    }

    fun load(name: String): String {
        val target: Plugin?
        val pluginDir = File("plugins")
        if (!pluginDir.isDirectory) {
            return LangConfig.generalPluginNotFound
        }
        var pluginFile = File(pluginDir, "$name.jar")
        if (!pluginFile.isFile) {
            for (f in pluginDir.listFiles()!!) {
                if (f.name.endsWith(".jar")) try {
                    val desc: PluginDescriptionFile =
                        github.gilbertokpl.total.TotalEssentials.instance.pluginLoader.getPluginDescription(f)
                    if (desc.name.equals(name, ignoreCase = true)) {
                        pluginFile = f
                        break
                    }
                } catch (e: InvalidDescriptionException) {
                    return LangConfig.generalPluginLoadProblems
                }
            }
        }
        target = try {
            Bukkit.getPluginManager().loadPlugin(pluginFile)
        } catch (e: InvalidDescriptionException) {
            return LangConfig.generalPluginLoadProblems
        } catch (e: InvalidPluginException) {
            return LangConfig.generalPluginLoadProblems
        }
        target!!.onLoad()
        Bukkit.getPluginManager().enablePlugin(target)
        return LangConfig.generalPluginLoad
    }

    fun reload(plugin: Plugin?, s: CommandSender) {
        if (plugin != null) {
            s.sendMessage(unload(plugin))
            s.sendMessage(load(plugin))
        }
    }

    fun unload(plugin: Plugin): String {
        val name = plugin.name
        val pluginManager = Bukkit.getPluginManager()
        var commandMap: SimpleCommandMap? = null
        var plugins: MutableList<Plugin?>? = null
        var names: MutableMap<String?, Plugin?>? = null
        var commands: MutableMap<String?, Command>? = null
        var listeners: Map<Event?, SortedSet<RegisteredListener>>? = null
        var reloadlisteners = true
        if (pluginManager != null) {
            pluginManager.disablePlugin(plugin)
            try {
                val pluginsField: Field = Bukkit.getPluginManager().javaClass.getDeclaredField("plugins")
                pluginsField.isAccessible = true
                plugins = pluginsField.get(pluginManager) as MutableList<Plugin?>
                val lookupNamesField: Field = Bukkit.getPluginManager().javaClass.getDeclaredField("lookupNames")
                lookupNamesField.isAccessible = true
                names = lookupNamesField.get(pluginManager) as MutableMap<String?, Plugin?>
                try {
                    val listenersField: Field = Bukkit.getPluginManager().javaClass.getDeclaredField("listeners")
                    listenersField.isAccessible = true
                    listeners = listenersField.get(pluginManager) as Map<Event?, SortedSet<RegisteredListener>>
                } catch (e: Throwable) {
                    reloadlisteners = false
                }
                val commandMapField: Field = Bukkit.getPluginManager().javaClass.getDeclaredField("commandMap")
                commandMapField.isAccessible = true
                commandMap = commandMapField.get(pluginManager) as SimpleCommandMap
                val knownCommandsField: Field = SimpleCommandMap::class.java.getDeclaredField("knownCommands")
                knownCommandsField.isAccessible = true
                commands = knownCommandsField.get(commandMap) as MutableMap<String?, Command>
            } catch (e: NoSuchFieldException) {
                return LangConfig.generalPluginUnloadProblems
            } catch (e: IllegalAccessException) {
                return LangConfig.generalPluginUnloadProblems
            }
        }
        pluginManager.disablePlugin(plugin)
        if (plugins != null && plugins.contains(plugin)) plugins.remove(plugin)
        if (names != null && names.containsKey(name)) names.remove(name)
        if (listeners != null && reloadlisteners) {
            for (set in listeners.values) {
                val it = set.iterator()
                while (it.hasNext()) {
                    val value = it.next()
                    if (value.plugin === plugin) it.remove()
                }
            }
        }
        if (commandMap != null) {
            val it: MutableIterator<Map.Entry<String?, Command>> = commands!!.entries.iterator()
            while (it.hasNext()) {
                val (_, value) = it.next()
                if (value is PluginCommand) {
                    if (value.plugin === plugin) {
                        value.unregister(commandMap as CommandMap)
                        it.remove()
                    }
                }
            }
        }
        val cl = plugin.javaClass.classLoader
        if (cl is URLClassLoader) {
            try {
                val pluginField: Field = cl.javaClass.getDeclaredField("plugin")
                pluginField.isAccessible = true
                pluginField.set(cl, null as Any?)
                val pluginInitField: Field = cl.javaClass.getDeclaredField("pluginInit")
                pluginInitField.isAccessible = true
                pluginInitField.set(cl, null as Any?)
            } catch (ignored: NoSuchFieldException) {
            } catch (ignored: SecurityException) {
            } catch (ignored: IllegalArgumentException) {
            } catch (ignored: IllegalAccessException) {
            }
            try {
                cl.close()
            } catch (ex: IOException) {
                //error
            }
        }
        System.gc()
        return LangConfig.generalPluginUnload
    }
}
