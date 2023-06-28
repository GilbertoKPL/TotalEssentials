package github.gilbertokpl.core.internal.task

import github.gilbertokpl.core.external.task.BukkitSchedulerController
import github.gilbertokpl.core.external.task.CoroutineTask
import github.gilbertokpl.core.external.task.SynchronizationContext
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import kotlin.coroutines.*

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

    val completion: Continuation<Unit> = object : Continuation<Unit> {
        override val context: CoroutineContext = controller.context
        override fun resumeWith(result: Result<Unit>) {
            if (result.isSuccess) {
                controller.resume(Unit)
            } else {
                controller.resumeWithException(result.exceptionOrNull()!!)
            }
        }
    }

    block.createCoroutine(controller, completion).resume(Unit)

    return CoroutineTask(controller)
}