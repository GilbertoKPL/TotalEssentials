package github.gilbertokpl.core.external.task

import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.core.internal.task.bukkitScheduler
import github.gilbertokpl.core.internal.task.schedule

class Task(lf: CorePlugin) {

    private val lunarFrame = lf

    fun sync(block: suspend BukkitSchedulerController.() -> Unit): CoroutineTask {
        return bukkitScheduler.schedule(lunarFrame.plugin, SynchronizationContext.SYNC, block)
    }

    fun async(block: suspend BukkitSchedulerController.() -> Unit): CoroutineTask {
        return bukkitScheduler.schedule(lunarFrame.plugin, SynchronizationContext.ASYNC, block)
    }

}