package github.gilbertokpl.total.cache.loop

import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.TaskUtil
import github.gilbertokpl.total.util.WorldUtil
import java.util.concurrent.TimeUnit

object ClearItemsLoop {
    fun start() {
        TaskUtil.getAnnounceExecutor().scheduleWithFixedDelay({
            WorldUtil.clearItems()
        }, MainConfig.ClearitemsTime.toLong(), MainConfig.ClearitemsTime.toLong(), TimeUnit.MINUTES)
    }
}