package github.gilbertokpl.total.cache.loop

import github.gilbertokpl.total.cache.internal.ShopInventory
import github.gilbertokpl.total.util.MoneyUtil
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object PluginLoop {
    fun start() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay({
            try {
                MoneyUtil.refreshTycoon()
            } catch (e: Exception) {
                println(e)
            }
            try {
                ShopInventory.setup()
            } catch (e: Exception) {
                println(e)
            }
        },5,5, TimeUnit.MINUTES)
    }
}