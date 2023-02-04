package github.gilbertokpl.total.util

import github.gilbertokpl.core.external.task.SynchronizationContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

internal object TaskUtil {

    private val poolExecutorTeleport = Executors.newCachedThreadPool()

    private var Executor = Executors.newSingleThreadScheduledExecutor()


    fun disable() {
        poolExecutorTeleport.shutdown()
        Executor.shutdown()
    }

    fun getAnnounceExecutor(): ScheduledExecutorService {
        return Executor
    }

    fun restartAnnounceExecutor() {
        Executor.shutdownNow()
        Executor = Executors.newSingleThreadScheduledExecutor()
    }

    fun getTeleportExecutor(): ExecutorService {
        return poolExecutorTeleport
    }


    fun teleportExecutor(time: Int): (() -> Unit) -> Unit {
        return {
            github.gilbertokpl.total.TotalEssentials.basePlugin.getTask().async {
                waitFor(time.toLong() * 20)
                try {
                    switchContext(SynchronizationContext.SYNC)
                    it()
                } catch (ex: Throwable) {

                }
            }
        }
    }
}
