package github.gilbertokpl.core.external.task

import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.core.internal.task.bukkitScheduler
import github.gilbertokpl.core.internal.task.schedule

class Task(core: CorePlugin) {

    private val corePlugin = core

    fun sync(block: suspend BukkitSchedulerController.() -> Unit): CoroutineTask {
        return bukkitScheduler.schedule(corePlugin.plugin, SynchronizationContext.SYNC, block)
    }

    fun async(block: suspend BukkitSchedulerController.() -> Unit): CoroutineTask {
        return bukkitScheduler.schedule(corePlugin.plugin, SynchronizationContext.ASYNC, block)
    }

}