package github.gilbertokpl.core.external.utils

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

object Executor {
    val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
}