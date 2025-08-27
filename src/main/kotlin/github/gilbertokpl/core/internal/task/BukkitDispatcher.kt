package github.gilbertokpl.core.internal.task

import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.function.Consumer
import kotlin.coroutines.CoroutineContext

internal val bukkitScheduler
    get() = Bukkit.getScheduler()

@OptIn(InternalCoroutinesApi::class)
class BukkitDispatcher(val plugin: Plugin, val async: Boolean = false) : CoroutineDispatcher(), Delay {

    private val foliaGlobalScheduler: Any?
    private val foliaGlobalRun: java.lang.reflect.Method?
    private val foliaAsyncScheduler: Any?
    private val foliaAsyncRunNow: java.lang.reflect.Method?

    init {
        val server = plugin.server
        val serverClass = server::class.java

        var gScheduler: Any? = null
        var gRun: java.lang.reflect.Method? = null
        var aScheduler: Any? = null
        var aRunNow: java.lang.reflect.Method? = null

        try {
            val gInstance = serverClass.getMethod("getGlobalRegionScheduler").invoke(server)
            gScheduler = gInstance
            gRun = gInstance.javaClass.getMethod("run", Plugin::class.java, Consumer::class.java)
        } catch (_: Exception) {
        }

        try {
            val aInstance = serverClass.getMethod("getAsyncScheduler").invoke(server)
            aScheduler = aInstance
            aRunNow = aInstance.javaClass.getMethod("runNow", Plugin::class.java, Consumer::class.java)
        } catch (_: Exception) {
        }

        foliaGlobalScheduler = gScheduler
        foliaGlobalRun = gRun
        foliaAsyncScheduler = aScheduler
        foliaAsyncRunNow = aRunNow
    }

    private fun runTask(block: Runnable): BukkitTask? {
        return if (foliaGlobalScheduler != null && foliaGlobalRun != null) {
            if (async && foliaAsyncScheduler != null && foliaAsyncRunNow != null) {
                foliaAsyncRunNow.invoke(foliaAsyncScheduler, plugin, Consumer<Any> { block.run() })
            } else {
                foliaGlobalRun.invoke(foliaGlobalScheduler, plugin, Consumer<Any> { block.run() })
            }
            null
        } else {
            if (async) {
                bukkitScheduler.runTaskAsynchronously(plugin, block)
            } else {
                bukkitScheduler.runTask(plugin, block)
            }
        }
    }

    private fun runTaskLater(block: Runnable, delayTicks: Long): BukkitTask? {
        return if (foliaGlobalScheduler != null) {
            // Folia não tem runLater, você teria que adaptar com delay + coroutine
            GlobalScope.launch {
                kotlinx.coroutines.delay(delayTicks * 50)
                runTask(block)
            }
            null
        } else {
            if (async) {
                bukkitScheduler.runTaskLaterAsynchronously(plugin, block, delayTicks)
            } else {
                bukkitScheduler.runTaskLater(plugin, block, delayTicks)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        val task = runTaskLater(Runnable {
            continuation.apply { resumeUndispatched(Unit) }
        }, timeMillis / 50)
        continuation.invokeOnCancellation { task?.cancel() }
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (!context.isActive) return

        if (!async && Bukkit.isPrimaryThread()) {
            block.run()
        } else {
            runTask(block)
        }
    }
}

fun Plugin.dispatcher(async: Boolean = false) = BukkitDispatcher(this, async)
