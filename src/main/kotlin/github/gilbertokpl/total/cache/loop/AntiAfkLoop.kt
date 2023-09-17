package github.gilbertokpl.total.cache.loop

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.local.SpawnData
import github.gilbertokpl.total.cache.local.WarpData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.TaskUtil
import java.util.concurrent.TimeUnit

object AntiAfkLoop {
    private val TIME_TO_CHECK_MINUTES = MainConfig.antiafkTimeToCheck
    private val TIME_TO_EXECUTE = MainConfig.antiafkTimeToExecute

    fun start() {
        TaskUtil.getInternalExecutor().scheduleWithFixedDelay(::checkPlayersAfk, TIME_TO_CHECK_MINUTES, TIME_TO_CHECK_MINUTES, TimeUnit.MINUTES)
    }

    private fun checkPlayersAfk() {
        for (player in TotalEssentials.basePlugin.getReflection().getPlayers()) {
            if (player.hasPermission("totalessentials.bypass.antiafk")) {
                continue
            }
            val afkTime = PlayerData.afk[player]?.plus(TIME_TO_CHECK_MINUTES.toInt()) ?: TIME_TO_CHECK_MINUTES.toInt()
            PlayerData.afk[player] = afkTime

            if (afkTime.toLong() >= TIME_TO_EXECUTE) {
                val warp = WarpData.warpLocation[MainConfig.antiafkWarp]

                if (warp == null) {
                    SpawnData.teleportToSpawn(player)
                }
                else {
                    player.teleport(warp)
                }

                PlayerData.afk[player] = 1
                player.sendMessage(LangConfig.antiafkMessage)
            }
        }
    }
}