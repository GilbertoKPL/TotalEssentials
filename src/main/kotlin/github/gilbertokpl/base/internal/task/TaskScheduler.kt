package github.gilbertokpl.base.internal.task

import github.gilbertokpl.base.external.task.SynchronizationContext
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask


internal interface TaskScheduler {

    val currentTask: BukkitTask?

    fun doWait(ticks: Long, task: (Long) -> Unit)

    fun doYield(task: (Long) -> Unit)

    fun doContextSwitch(context: SynchronizationContext, task: (Boolean) -> Unit)

    fun forceNewContext(context: SynchronizationContext, task: () -> Unit)

}

internal class NonRepeatingTaskScheduler(val plugin: Plugin, val scheduler: BukkitScheduler) : TaskScheduler {

    override var currentTask: BukkitTask? = null

    override fun doWait(ticks: Long, task: (Long) -> Unit) {
        runTaskLater(ticks) { task(ticks) }
    }

    override fun doYield(task: (Long) -> Unit) {
        doWait(0, task)
    }

    //TODO Be lazy if not yet started
    override fun doContextSwitch(context: SynchronizationContext, task: (Boolean) -> Unit) {
        val currentContext = currentContext()
        if (context == currentContext) {
            task(false)
        } else {
            forceNewContext(context) { task(true) }
        }
    }

    override fun forceNewContext(context: SynchronizationContext, task: () -> Unit) {
        runTask(context) { task() }
    }

    private fun runTask(context: SynchronizationContext = currentContext(), task: () -> Unit) {
        currentTask = when (context) {
            SynchronizationContext.SYNC -> scheduler.runTask(plugin, task)
            SynchronizationContext.ASYNC -> scheduler.runTaskAsynchronously(plugin, task)
        }
    }

    private fun runTaskLater(ticks: Long, context: SynchronizationContext = currentContext(), task: () -> Unit) {
        currentTask = when (context) {
            SynchronizationContext.SYNC -> scheduler.runTaskLater(plugin, task, ticks)
            SynchronizationContext.ASYNC -> scheduler.runTaskLaterAsynchronously(plugin, task, ticks)
        }
    }

}

private class RepetitionContinuation(val resume: (Long) -> Unit, val delay: Long = 0) {
    var passedTicks = 0L
    private var resumed = false

    fun tryResume(passedTicks: Long) {
        if (resumed) {
            throw IllegalStateException("Already resumed")
        }
        this.passedTicks += passedTicks
        if (this.passedTicks >= delay) {
            resumed = true
            resume(this.passedTicks)
        }
    }
}

internal class RepeatingTaskScheduler(
    val interval: Long,
    val plugin: Plugin,
    val scheduler: BukkitScheduler
) : TaskScheduler {

    override var currentTask: BukkitTask? = null
    private var nextContinuation: RepetitionContinuation? = null

    override fun doWait(ticks: Long, task: (Long) -> Unit) {
        nextContinuation = RepetitionContinuation(task, ticks)
    }

    override fun doYield(task: (Long) -> Unit) {
        nextContinuation = RepetitionContinuation(task)
    }

    //TODO Be lazy if not yet started...maybe?
    override fun doContextSwitch(context: SynchronizationContext, task: (Boolean) -> Unit) {
        val currentContext = currentContext()
        if (context == currentContext) {
            task(false)
        } else {
            forceNewContext(context) { task(true) }
        }
    }

    override fun forceNewContext(context: SynchronizationContext, task: () -> Unit) {
        doYield { task() }
        runTaskTimer(context)
    }

    private fun runTaskTimer(context: SynchronizationContext) {
        currentTask?.cancel()
        val task: () -> Unit = { nextContinuation?.tryResume(interval) }
        currentTask = when (context) {
            SynchronizationContext.SYNC -> scheduler.runTaskTimer(plugin, task, 0L, interval)
            SynchronizationContext.ASYNC -> scheduler.runTaskTimerAsynchronously(plugin, task, 0L, interval)
        }
    }

}

fun currentContext() = if (Bukkit.isPrimaryThread()) SynchronizationContext.SYNC else SynchronizationContext.ASYNC