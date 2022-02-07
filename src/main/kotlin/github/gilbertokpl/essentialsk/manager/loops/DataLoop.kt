package github.gilbertokpl.essentialsk.manager.loops

import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.util.MainUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import java.util.concurrent.TimeUnit

internal object DataLoop {
    fun start() {
        TaskUtil.getDataExecutor().scheduleWithFixedDelay({
            MainUtil.consoleMessage(LangConfig.generalSaveDataMessage)
            DataManager.save()
            MainUtil.consoleMessage(LangConfig.generalSaveDataSuccess)
        }, 10, 10, TimeUnit.MINUTES)
    }
}
