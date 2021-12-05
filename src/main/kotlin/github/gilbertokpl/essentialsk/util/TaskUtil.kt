package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.manager.IInstance
import org.apache.commons.lang3.exception.ExceptionUtils
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TaskUtil {

    internal val asyncExecutor = asyncFuture()

    internal val commandExecutor = commandExecutor()

    private val poolExecutor = Executors.newCachedThreadPool()

    fun disable() {
        poolExecutor.shutdown()
    }

    fun getExecutor(): ExecutorService {
        return poolExecutor
    }

    private fun commandExecutor(): (() -> Unit) -> Unit {
        return {
            try {
                it()
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
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