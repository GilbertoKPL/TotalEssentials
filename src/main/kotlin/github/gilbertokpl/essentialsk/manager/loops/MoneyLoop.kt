package github.gilbertokpl.essentialsk.manager.loops

import github.gilbertokpl.essentialsk.util.MoneyUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import java.util.concurrent.TimeUnit

object MoneyLoop {

    fun start() {
        TaskUtil.getMoneyExecutor().scheduleWithFixedDelay({
            MoneyUtil.refreashTycoon()
        }, 1, 1, TimeUnit.MINUTES)
    }
}