package github.gilbertokpl.core.internal.task

import github.gilbertokpl.core.external.task.BukkitSchedulerController
import github.gilbertokpl.core.external.task.CoroutineTask
import github.gilbertokpl.core.external.task.SynchronizationContext
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import kotlin.coroutines.createCoroutine
import kotlin.coroutines.resume

internal fun BukkitScheduler.schedule(
    plugin: Plugin,
    initialContext: SynchronizationContext = SynchronizationContext.SYNC,
    co: suspend BukkitSchedulerController.() -> Unit
): CoroutineTask {
    val controller = BukkitSchedulerController(plugin, this)
    val block: suspend BukkitSchedulerController.() -> Unit = {
        try {
            start(initialContext)
            co()
        } finally {
            cleanup()
        }
    }

    block.createCoroutine(receiver = controller, completion = controller).resume(Unit)

    return CoroutineTask(controller)
}