package github.gilbertokpl.total.cache.loop

import github.gilbertokpl.total.cache.internal.inventory.Playtime
import github.gilbertokpl.total.cache.internal.inventory.Shop
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.MoneyUtil
import github.gilbertokpl.total.util.TaskUtil
import java.util.concurrent.TimeUnit

object PluginLoop {

    fun start() {
        TaskUtil.getInternalExecutor().scheduleWithFixedDelay({
            refreshMoney()
            setupShopInventory()
            setupPlaytimeInventory()
            setupDiscordLoop()
        }, 5, 5, TimeUnit.MINUTES)
    }

    private fun refreshMoney() {
        if (MainConfig.moneyActivated) {
            try {
                MoneyUtil.refreshTycoon()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupShopInventory() {
        if (MainConfig.shopActivated) {
            try {
                Shop.setup()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupPlaytimeInventory() {
        if (MainConfig.playtimeActivated) {
            try {
                Playtime.setup()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupDiscordLoop() {
        if (MainConfig.discordbotSendTopicUpdate) {
            try {
                DiscordLoop.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}