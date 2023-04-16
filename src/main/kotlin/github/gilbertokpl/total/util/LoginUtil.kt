package github.gilbertokpl.total.util

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.local.LoginData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit

object LoginUtil {
    fun loginMessage(player: Player) {
        TaskUtil.getInternalExecutor().scheduleWithFixedDelay({
            if (!player.isOnline || LoginData.isPlayerLoggedIn(player)) {
                Thread.currentThread().stop()
            }
            if ((MainConfig.authMaxAttempts + 1) == LoginData.values[player]) {
                Bukkit.getScheduler().runTask(TotalEssentials.instance, Runnable {
                    player.kickPlayer(LangConfig.authKickMessageTime)
                })
                Thread.currentThread().stop()
            }
            if (LoginData.doesPlayerExist(player)) {
                player.sendMessage(LangConfig.authLoginMessage)
            } else {
                player.sendMessage(LangConfig.authRegisterMessage)
            }
            LoginData.values[player] = LoginData.values[player]?.plus(1)!!
        }, 0, 10, TimeUnit.SECONDS)
    }
}