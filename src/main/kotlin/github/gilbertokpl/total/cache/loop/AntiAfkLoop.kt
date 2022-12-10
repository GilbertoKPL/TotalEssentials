package github.gilbertokpl.total.cache.loop

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.local.SpawnData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.TaskUtil
import java.util.concurrent.TimeUnit

object AntiAfkLoop {
    fun start() {
        TaskUtil.getAnnounceExecutor().scheduleWithFixedDelay({
            for (p in TotalEssentials.basePlugin.getReflection().getPlayers()) {
                if (p.hasPermission("totalessentials.bypass.antiafk")) continue

                if (PlayerData.afk[p]!! >= (MainConfig.antiafkTimeToExecute / MainConfig.antiafkTimeToCheck)) {
                    SpawnData.teleport(p)
                    p.sendMessage(LangConfig.antiafkMessage)
                    continue
                }

                PlayerData.afk[p] = PlayerData.afk[p]!!.plus(1)
            }
        }, MainConfig.antiafkTimeToCheck.toLong(), MainConfig.antiafkTimeToCheck.toLong(), TimeUnit.MINUTES)
    }
}