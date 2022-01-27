package github.okkero.skedule

import github.gilbertokpl.essentialsk.EssentialsK
import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import kotlin.coroutines.CoroutineContext

private val bukkitScheduler
    get() = Bukkit.getScheduler()


@OptIn(InternalCoroutinesApi::class)
class BukkitDispatcher(private val async: Boolean = false) : CoroutineDispatcher(), Delay {

    private val runTaskLater: (Plugin, Runnable, Long) -> BukkitTask =
        if (async)
            bukkitScheduler::runTaskLaterAsynchronously
        else
            bukkitScheduler::runTaskLater
    private val runTask: (Plugin, Runnable) -> BukkitTask =
        if (async)
            bukkitScheduler::runTaskAsynchronously
        else
            bukkitScheduler::runTask

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        val task = runTaskLater(
            EssentialsK.instance,
            Runnable {
                continuation.apply { resumeUndispatched(Unit) }
            },
            timeMillis / 50
        )
        continuation.invokeOnCancellation { task.cancel() }
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (!context.isActive) {
            return
        }

        if (!async && Bukkit.isPrimaryThread()) {
            block.run()
        } else {
            runTask(EssentialsK.instance, block)
        }
    }

}

fun JavaPlugin.dispatcher(async: Boolean = false) = BukkitDispatcher(async)