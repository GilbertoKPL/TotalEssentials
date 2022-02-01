package github.gilbertokpl.essentialsk.loops

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.util.MainUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import java.util.concurrent.TimeUnit

object DataLoop {
    fun start() {
        TaskUtil.getDataExecutor().scheduleWithFixedDelay({
            MainUtil.consoleMessage(GeneralLang.generalSaveDataMessage)
            DataManager.save()
            MainUtil.consoleMessage(GeneralLang.generalSaveDataSuccess)
        }, 10, 10, TimeUnit.MINUTES)
    }
}