package github.gilbertokpl.total.util

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.local.LoginData
import github.gilbertokpl.total.cache.local.SpawnData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit

object LoginUtil {
    fun loginMessage(player: Player) {
        TaskUtil.getAnnounceExecutor().scheduleWithFixedDelay({
            if (!player.isOnline || LoginData.checkIfPlayerIsLoggedIn(player)) {
                Thread.currentThread().stop()
            }
            if ((MainConfig.authMaxAttempts + 1) == LoginData.values[player]) {
                Bukkit.getScheduler().runTask(TotalEssentials.instance, Runnable {
                    player.kickPlayer(LangConfig.authKickMessageTime)
                })
                Thread.currentThread().stop()
            }
            if (LoginData.checkIfPlayerExist(player)) {
                player.sendMessage(LangConfig.authLoginMessage)
            }
            else {
                player.sendMessage(LangConfig.authRegisterMessage)
            }
            LoginData.values[player] = LoginData.values[player]?.plus(1)!!
        }, 0, 10, TimeUnit.SECONDS)

        TaskUtil.getAnnounceExecutor().scheduleWithFixedDelay({
            if (!player.isOnline) {
                Thread.currentThread().stop()
            }
            if (LoginData.checkIfPlayerIsLoggedIn(player)) {
                SpawnData.teleport(player)
                Thread.currentThread().stop()
            }
            SpawnData.teleport(player)
        }, 100000, 100000, TimeUnit.MICROSECONDS)
    }
}