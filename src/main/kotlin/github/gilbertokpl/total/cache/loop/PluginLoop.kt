package github.gilbertokpl.total.cache.loop

import github.gilbertokpl.total.cache.internal.PlaytimeInventory
import github.gilbertokpl.total.cache.internal.ShopInventory
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.MoneyUtil
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object PluginLoop {
    fun start() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay({
            try {
                if (MainConfig.moneyActivated) {

                }
            } catch (e: Exception) {
                println(e)
            }
            try {
                if (MainConfig.shopActivated) {
                    ShopInventory.setup()
                }
            } catch (e: Exception) {
                println(e)
            }
            try {
                if (MainConfig.playtimeActivated) {
                    PlaytimeInventory.setup()
                }
            } catch (e: Exception) {
                println(e)
            }
            try {
                if (MainConfig.discordbotSendTopicUpdate) {
                    DiscordLoop.setup()
                }
            } catch (e: Exception) {
                println(e)
            }
        },5,5, TimeUnit.MINUTES)
    }
}