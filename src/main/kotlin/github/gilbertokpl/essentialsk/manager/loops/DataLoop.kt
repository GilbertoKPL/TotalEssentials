package github.gilbertokpl.essentialsk.manager.loops

import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.util.TaskUtil
import java.util.concurrent.TimeUnit

internal object DataLoop {
    fun start() {
        TaskUtil.getDataExecutor().scheduleWithFixedDelay({
            DataManager.save()
        }, 10, 10, TimeUnit.MINUTES)
    }
}
