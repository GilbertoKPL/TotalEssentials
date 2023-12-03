package github.gilbertokpl.total.util

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

internal object TaskUtil {

    private var Executor = Executors.newSingleThreadScheduledExecutor()

    fun disable() {
        Executor.shutdown()
    }

    fun getInternalExecutor(): ScheduledExecutorService {
        return Executor
    }

    fun restartInternalExecutor() {
        Executor.shutdownNow()
        Executor = Executors.newSingleThreadScheduledExecutor()
    }
}
