package github.gilbertokpl.total.cache.loop

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.local.SpawnData
import github.gilbertokpl.total.cache.local.WarpData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.TaskUtil
import java.util.concurrent.TimeUnit

/**
 * Classe que executa um loop anti-AFK.
 * Quando um jogador estiver AFK por um determinado período de tempo, ele será teletransportado e receberá uma mensagem.
 */
object AntiAfkLoop {
    private val TIME_TO_CHECK_MINUTES = MainConfig.antiafkTimeToCheck
    private val TIME_TO_EXECUTE = MainConfig.antiafkTimeToExecute

    /**
     * Inicia o loop anti-AFK.
     */
    fun start() {
        TaskUtil.getAnnounceExecutor().scheduleWithFixedDelay(::checkPlayersAfk, TIME_TO_CHECK_MINUTES, TIME_TO_CHECK_MINUTES, TimeUnit.MINUTES)
    }

    /**
     * Verifica se os jogadores estão AFK e executa a ação apropriada se necessário.
     */
    private fun checkPlayersAfk() {
        TotalEssentials.basePlugin.getReflection().getPlayers().forEach { player ->
            if (player.hasPermission("totalessentials.bypass.antiafk")) {
                return@forEach // continua com o próximo jogador se o jogador tiver permissão para ignorar o anti-AFK.
            }

            val afkTime = PlayerData.afk[player]?.plus(1) ?: 1
            PlayerData.afk[player] = afkTime

            if (afkTime >= TIME_TO_EXECUTE / TIME_TO_CHECK_MINUTES) {
                val warp = WarpData.warpLocation[MainConfig.antiafkWarp]

                if (warp == null) {
                    SpawnData.teleportToSpawn(player)
                }
                else {
                    player.teleport(warp)
                }

                player.sendMessage(LangConfig.antiafkMessage)
            }
        }
    }
}