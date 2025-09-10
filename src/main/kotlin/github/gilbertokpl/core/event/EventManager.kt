package github.gilbertokpl.core.event

import github.gilbertokpl.core.TotalCore
import org.bukkit.event.Listener

class EventManager(private val totalCore: TotalCore) {

    fun start(packageName: String) {
        val classes = totalCore.getReflection().getClasses(packageName)
        classes.forEach { clazz ->
            try {
                val instance = clazz.getDeclaredConstructor().newInstance() as? Listener
                instance?.let {
                    totalCore.plugin.server.pluginManager.registerEvents(it, totalCore.plugin)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}