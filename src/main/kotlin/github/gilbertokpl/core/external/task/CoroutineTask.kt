package github.gilbertokpl.core.external.task

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import kotlin.coroutines.resume

class CoroutineTask internal constructor(private val controller: BukkitSchedulerController) {

    val plugin: Plugin
        get() = controller.plugin
    val currentTask: BukkitTask?
        get() = controller.currentTask
    val isSync: Boolean
        get() = controller.currentTask?.isSync ?: false
    val isAsync: Boolean
        get() = !(controller.currentTask?.isSync ?: true)

    fun cancel() {
        controller.resume(Unit)
    }
}