package github.gilbertokpl.total.cache.loop

import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.TaskUtil
import github.gilbertokpl.total.util.WorldUtil
import java.util.concurrent.TimeUnit

object ClearEntitiesLoop {
    private val CLEAR_ITEMS_INTERVAL_MINUTES = MainConfig.clearentitiesTime.toLong()
    fun start() {
        TaskUtil.getInternalExecutor().scheduleWithFixedDelay(
            ::clearItems,
            CLEAR_ITEMS_INTERVAL_MINUTES,
            CLEAR_ITEMS_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        )
    }

    private fun clearItems() {
        WorldUtil.clearEntities()
    }
}