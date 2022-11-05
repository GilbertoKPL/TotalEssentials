package github.gilbertokpl.core.external.task

import github.gilbertokpl.core.internal.task.NonRepeatingTaskScheduler
import github.gilbertokpl.core.internal.task.RepeatingTaskScheduler
import github.gilbertokpl.core.internal.task.TaskScheduler
import github.gilbertokpl.core.internal.task.currentContext
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import kotlin.coroutines.*


/**
 * Controller for Bukkit Scheduler coroutine
 *
 * @property plugin the Plugin instance to schedule the tasks bound to this coroutine
 * @property scheduler the BukkitScheduler instance to schedule the tasks bound to this coroutine
 * @property currentTask the task that is currently executing within the context of this coroutine
 * @property isRepeating whether this coroutine is currently backed by a repeating task
 */
class BukkitSchedulerController(val plugin: Plugin, val scheduler: BukkitScheduler) : Continuation<Unit> {
    override val context: CoroutineContext
        get() = EmptyCoroutineContext

    private var schedulerDelegate: TaskScheduler = NonRepeatingTaskScheduler(plugin, scheduler)

    val currentTask: BukkitTask?
        get() = schedulerDelegate.currentTask
    val isRepeating: Boolean
        get() = schedulerDelegate is RepeatingTaskScheduler


    internal suspend fun start(initialContext: SynchronizationContext) = suspendCoroutine<Unit> { cont ->
        schedulerDelegate.doContextSwitch(initialContext) { cont.resume(Unit) }
    }

    internal fun cleanup() {
        currentTask?.cancel()
    }

    override fun resumeWith(result: Result<Unit>) {
        cleanup()
        result.getOrThrow()
    }

    /**
     * Wait for __at least__ the specified amount of ticks. If the coroutine is currently backed by a non-repeating
     * task, a new Bukkit task will be scheduled to run the specified amount of ticks later. If this coroutine is
     * currently backed by a repeating task, the amount of ticks waited depends on the repetition resolution of the
     * coroutine. For example, if the repetition resolution is `10` and the `ticks` argument is `12`, it will result in
     * a delay of `20` ticks.
     *
     * @param ticks the amount of ticks to __at least__ wait for
     *
     * @return the actual amount of ticks waited
     */
    suspend fun waitFor(ticks: Long): Long = suspendCoroutine { cont ->
        schedulerDelegate.doWait(ticks, cont::resume)
    }

    /**
     * Relinquish control for as short an amount of time as possible. That is, wait for as few ticks as possible.
     * If this coroutine is currently backed by a non-repeating task, this will result in a task running at the next
     * possible occasion. If this coroutine is currently backed by a repeating task, this will result in a delay for as
     * short an amount of ticks as the repetition resolution allows.
     *
     * @return the actual amount of ticks waited
     */
    suspend fun yield(): Long = suspendCoroutine { cont ->
        schedulerDelegate.doYield(cont::resume)
    }

    /**
     * Switch to the specified SynchronizationContext. If this coroutine is already in the given context, this method
     * does nothing and returns immediately. Otherwise, the behaviour is documented in [newContext].
     *
     * @param context the context to switch to
     * @return `true` if a context switch was made, `false` otherwise
     */
    suspend fun switchContext(context: SynchronizationContext): Boolean = suspendCoroutine { cont ->
        schedulerDelegate.doContextSwitch(context, cont::resume)
    }

    /**
     * Force a new task to be scheduled in the specified context. This method will result in a new repeating or
     * non-repeating task to be scheduled. Repetition state and resolution is determined by the currently running currentTask.
     *
     * @param context the synchronization context of the new task
     */
    suspend fun newContext(context: SynchronizationContext): Unit = suspendCoroutine { cont ->
        schedulerDelegate.forceNewContext(context) { cont.resume(Unit) }
    }

    /**
     * Turn this coroutine into a repeating coroutine. This method will result in a new repeating task being scheduled.
     * The new task's interval will be the same as the specified resolution. Subsequent calls to [waitFor] and [yield]
     * will from here on out defer further execution to the next iteration of the repeating task. This is useful for
     * things like countdowns and delays at fixed intervals, since [waitFor] will not result in a new task being
     * spawned.
     */
    suspend fun repeating(resolution: Long): Long = suspendCoroutine { cont ->
        schedulerDelegate = RepeatingTaskScheduler(resolution, plugin, scheduler)
        schedulerDelegate.forceNewContext(currentContext()) { cont.resume(0) }
    }

}