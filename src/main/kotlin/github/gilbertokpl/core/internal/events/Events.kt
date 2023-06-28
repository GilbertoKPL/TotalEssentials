package github.gilbertokpl.core.internal.events

import github.gilbertokpl.core.external.CorePlugin
import org.bukkit.event.Listener

class Events(private val corePlugin: CorePlugin) {

    fun start(packageName: String) {
        val classes = corePlugin.getReflection().getClasses(packageName)
        classes.forEach { clazz ->
            try {
                val instance = clazz.getDeclaredConstructor().newInstance() as? Listener
                instance?.let {
                    corePlugin.plugin.server.pluginManager.registerEvents(it, corePlugin.plugin)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}