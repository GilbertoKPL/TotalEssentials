package github.gilbertokpl.total.util

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.LangConfig
import org.bukkit.Bukkit
import org.bukkit.command.*
import org.bukkit.event.Event
import org.bukkit.plugin.*
import java.io.File
import java.lang.reflect.Field
import java.net.URLClassLoader
import java.util.*

object PluginUtil {

    private const val PLUGINS_FOLDER = "plugins"
    private const val JAR_EXTENSION = ".jar"

    /**
     * Busca um plugin pelo nome (case-insensitive)
     */
    fun getPluginByName(name: String): Plugin? {
        return Bukkit.getPluginManager().plugins.firstOrNull { plugin ->
            plugin.name.equals(name, ignoreCase = true)
        }
    }

    /**
     * Habilita um plugin se ele estiver desabilitado
     */
    fun enable(plugin: Plugin?) {
        if (plugin != null && !plugin.isEnabled) {
            Bukkit.getPluginManager().enablePlugin(plugin)
        }
    }

    /**
     * Desabilita um plugin se ele estiver habilitado
     */
    fun disable(plugin: Plugin?) {
        if (plugin != null && plugin.isEnabled) {
            Bukkit.getPluginManager().disablePlugin(plugin)
        }
    }

    /**
     * Carrega um plugin a partir do seu arquivo JAR
     */
    fun load(pluginName: String): String {
        val pluginsDirectory = File(PLUGINS_FOLDER)

        if (!pluginsDirectory.isDirectory) {
            return LangConfig.generalPluginNotFound
        }

        val pluginFile = findPluginFile(pluginsDirectory, pluginName)
            ?: return LangConfig.generalPluginNotFound

        return loadPluginFromFile(pluginFile)
    }

    /**
     * Recarrega um plugin (descarrega e carrega novamente)
     */
    fun reload(plugin: Plugin?, sender: CommandSender) {
        if (plugin == null) return

        sender.sendMessage(unload(plugin))
        sender.sendMessage(load(plugin.name))
    }

    /**
     * Descarrega completamente um plugin da memória
     */
    fun unload(plugin: Plugin): String {
        val pluginManager = Bukkit.getPluginManager()

        pluginManager.disablePlugin(plugin)

        val reflectionResult = PluginReflectionHelper.unregisterPlugin(plugin, pluginManager)
        if (!reflectionResult) {
            return LangConfig.generalPluginUnloadProblems
        }

        cleanupClassLoader(plugin)
        System.gc()

        return LangConfig.generalPluginUnload
    }

    private fun findPluginFile(pluginsDirectory: File, pluginName: String): File? {
        // Tenta encontrar diretamente por nome
        val directFile = File(pluginsDirectory, "$pluginName$JAR_EXTENSION")
        if (directFile.isFile) return directFile

        // Procura em todos os JARs do diretório
        return pluginsDirectory.listFiles()?.firstOrNull { file ->
            if (!file.name.endsWith(JAR_EXTENSION)) return@firstOrNull false

            try {
                val description = TotalEssentials.getInstance().pluginLoader.getPluginDescription(file)
                description.name.equals(pluginName, ignoreCase = true)
            } catch (e: InvalidDescriptionException) {
                false
            }
        }
    }

    private fun loadPluginFromFile(pluginFile: File): String {
        val plugin = try {
            Bukkit.getPluginManager().loadPlugin(pluginFile)
        } catch (e: Exception) {
            return LangConfig.generalPluginLoadProblems
        } ?: return LangConfig.generalPluginLoadProblems

        plugin.onLoad()
        Bukkit.getPluginManager().enablePlugin(plugin)

        return LangConfig.generalPluginLoad
    }

    private fun cleanupClassLoader(plugin: Plugin) {
        val classLoader = plugin.javaClass.classLoader as? URLClassLoader ?: return

        try {
            // Limpa referências internas do ClassLoader
            setFieldValue(classLoader, "plugin", null)
            setFieldValue(classLoader, "pluginInit", null)

            // Fecha o ClassLoader
            classLoader.close()
        } catch (e: Exception) {
            // Ignorar erros de cleanup - não crítico
        }
    }

    private fun setFieldValue(target: Any, fieldName: String, value: Any?) {
        try {
            val field = target.javaClass.getDeclaredField(fieldName)
            field.isAccessible = true
            field.set(target, value)
        } catch (e: Exception) {
            // Field não existe nesta versão/implementação
        }
    }

    /**
     * Helper class para manipular internals do PluginManager via reflection
     */
    private object PluginReflectionHelper {

        private val pluginsField by lazy { getField(PluginManager::class.java, "plugins") }
        private val lookupNamesField by lazy { getField(PluginManager::class.java, "lookupNames") }
        private val listenersField by lazy { getField(PluginManager::class.java, "listeners") }
        private val commandMapField by lazy { getField(PluginManager::class.java, "commandMap") }
        private val knownCommandsField by lazy { getField(SimpleCommandMap::class.java, "knownCommands") }

        fun unregisterPlugin(plugin: Plugin, pluginManager: PluginManager): Boolean {
            return try {
                removeFromPluginsList(plugin, pluginManager)
                removeFromLookupNames(plugin, pluginManager)
                removeFromListeners(plugin, pluginManager)
                removeFromCommandMap(plugin, pluginManager)
                true
            } catch (e: Exception) {
                false
            }
        }

        private fun removeFromPluginsList(plugin: Plugin, pluginManager: PluginManager) {
            val pluginsList = getFieldValue<MutableList<Plugin>>(pluginManager, pluginsField) ?: return
            pluginsList.remove(plugin)
        }

        private fun removeFromLookupNames(plugin: Plugin, pluginManager: PluginManager) {
            val lookupNames = getFieldValue<MutableMap<String, Plugin>>(pluginManager, lookupNamesField) ?: return
            lookupNames.remove(plugin.name)
        }

        private fun removeFromListeners(plugin: Plugin, pluginManager: PluginManager) {
            val listeners = getFieldValue<Map<Event, SortedSet<RegisteredListener>>>(
                pluginManager,
                listenersField
            ) ?: return

            listeners.values.forEach { listenerSet ->
                listenerSet.removeIf { it.plugin === plugin }
            }
        }

        private fun removeFromCommandMap(plugin: Plugin, pluginManager: PluginManager) {
            val commandMap = getFieldValue<SimpleCommandMap>(pluginManager, commandMapField) ?: return
            val knownCommands = getFieldValue<MutableMap<String, Command>>(commandMap, knownCommandsField) ?: return

            knownCommands.entries.removeIf { (_, command) ->
                if (command is PluginCommand && command.plugin === plugin) {
                    command.unregister(commandMap)
                    true
                } else {
                    false
                }
            }
        }

        private fun getField(clazz: Class<*>, fieldName: String): Field? {
            return try {
                clazz.getDeclaredField(fieldName).apply { isAccessible = true }
            } catch (e: NoSuchFieldException) {
                null
            }
        }

        @Suppress("UNCHECKED_CAST")
        private fun <T> getFieldValue(target: Any, field: Field?): T? {
            if (field == null) return null
            return try {
                field.get(target) as? T
            } catch (e: Exception) {
                null
            }
        }
    }
}