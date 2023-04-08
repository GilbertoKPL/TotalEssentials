package github.gilbertokpl.core.internal.events

import github.gilbertokpl.core.external.CorePlugin
import org.bukkit.event.Listener

class Events(lf: CorePlugin) {

    private val corePlugin = lf

    fun start(packageName: String) {
        val classes = corePlugin.getReflection().getClasses(packageName)
        classes.forEach {
            try {
                corePlugin.plugin.server.pluginManager.registerEvents(
                    it.getDeclaredConstructor().newInstance() as Listener,
                    corePlugin.plugin
                )
            } catch (e: NoClassDefFoundError) {
                e.printStackTrace()
            }
        }
    }
}