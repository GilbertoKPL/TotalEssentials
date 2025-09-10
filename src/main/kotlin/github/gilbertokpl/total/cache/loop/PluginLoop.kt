package github.gilbertokpl.total.cache.loop

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.inventory.Playtime
import github.gilbertokpl.total.cache.inventory.Shop
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.economy.MoneyManager

import java.util.concurrent.TimeUnit

object PluginLoop {

    fun start() {
        TotalEssentials.getCore().getTask().getInternalExecutor().scheduleWithFixedDelay({
            refreshMoney()
            setupShopInventory()
            setupPlaytimeInventory()
            setupDiscordLoop()
        }, 5, 5, TimeUnit.MINUTES)
    }

    private fun refreshMoney() {
        if (MainConfig.moneyActivated) {
            try {
                MoneyManager.refreshTycoon()
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