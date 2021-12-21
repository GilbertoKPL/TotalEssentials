package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.manager.IInstance
import org.apache.commons.lang3.exception.ExceptionUtils
import java.util.concurrent.*


class TaskUtil {

    internal val asyncExecutor = asyncFuture()

    private val poolExecutor = Executors.newCachedThreadPool()

    private val poolExecutorTeleport = Executors.newCachedThreadPool()

    private var announceExecutor = Executors.newSingleThreadScheduledExecutor()

    fun disable() {
        poolExecutor.shutdown()
        poolExecutorTeleport.shutdown()
        announceExecutor.shutdown()
    }

    fun getAnnounceExecutor(): ScheduledExecutorService {
        return announceExecutor
    }

    fun restartAnnounceExecutor() {
        announceExecutor.shutdownNow()
        announceExecutor = Executors.newSingleThreadScheduledExecutor()
    }

    fun getTeleportExecutor(): ExecutorService {
        return poolExecutorTeleport
    }

    fun getExecutor(): ExecutorService {
        return poolExecutor
    }

    fun teleportExecutor(time: Int): (() -> Unit) -> Unit {
        return {
            CompletableFuture.runAsync({
                TimeUnit.SECONDS.sleep(time.toLong())
                try {
                    EssentialsK.instance.server.scheduler.runTask(EssentialsK.instance, Runnable { it() })
                } catch (ex: Exception) {
                    FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
                }
            }, poolExecutorTeleport)
        }
    }

    private fun asyncFuture(): (() -> Unit) -> Unit {
        return {
            CompletableFuture.runAsync({
                try {
                    it()
                } catch (ex: Exception) {
                    FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
                }
            }, poolExecutor)
        }
    }

    companion object : IInstance<TaskUtil> {
        private val instance = createInstance()
        override fun createInstance(): TaskUtil = TaskUtil()
        override fun getInstance(): TaskUtil {
            return instance
        }
    }
}