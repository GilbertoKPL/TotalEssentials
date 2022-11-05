package github.gilbertokpl.core.internal.events

import github.gilbertokpl.core.external.CorePlugin
import org.bukkit.event.Listener

class Events(lf: CorePlugin) {

    private val lunarFrame = lf

    fun start(packageName: String) {
        val classes = lunarFrame.getReflection().getClasses(packageName)
        classes.forEach {
            lunarFrame.plugin.server.pluginManager.registerEvents(
                it.getDeclaredConstructor().newInstance() as Listener,
                lunarFrame.plugin
            )
        }
    }
}