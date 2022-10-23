package github.gilbertokpl.base.external.task

import github.gilbertokpl.base.external.BasePlugin
import github.gilbertokpl.base.internal.task.bukkitScheduler
import github.gilbertokpl.base.internal.task.schedule

class Task(lf: BasePlugin) {

    private val lunarFrame = lf

    fun sync(block: suspend BukkitSchedulerController.() -> Unit): CoroutineTask {
        return bukkitScheduler.schedule(lunarFrame.plugin, SynchronizationContext.SYNC, block)
    }

    fun async(block: suspend BukkitSchedulerController.() -> Unit): CoroutineTask {
        return bukkitScheduler.schedule(lunarFrame.plugin, SynchronizationContext.ASYNC, block)
    }

}