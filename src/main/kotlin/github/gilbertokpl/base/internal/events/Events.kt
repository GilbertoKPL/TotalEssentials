package github.gilbertokpl.base.internal.events

import github.gilbertokpl.base.external.BasePlugin
import org.bukkit.event.Listener

class Events(lf: BasePlugin) {

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