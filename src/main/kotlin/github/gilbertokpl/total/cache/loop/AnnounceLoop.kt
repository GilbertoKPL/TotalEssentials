package github.gilbertokpl.total.cache.loop

import  github.gilbertokpl.total.cache.internal.OtherConfig
import github.gilbertokpl.total.util.TaskUtil
import org.bukkit.Bukkit
import java.util.concurrent.TimeUnit

/**
 * Classe que executa um loop de anúncios.
 * Envia anúncios para todos os jogadores a cada intervalo de tempo especificado.
 */
internal object AnnounceLoop {
    private var currentAnnouncementIndex = 0
    private var maxAnnouncementIndex = 0

    /**
     * Inicia o loop de anúncios.
     * @param maxAnnouncements O número máximo de anúncios disponíveis.
     * @param intervalInMinutes O intervalo de tempo em minutos para enviar anúncios.
     */
    fun start(maxAnnouncements: Int, intervalInMinutes: Int) {
        maxAnnouncementIndex = maxAnnouncements

        TaskUtil.getInternalExecutor().scheduleWithFixedDelay(::sendAnnouncement, intervalInMinutes * 20L, intervalInMinutes * 20L, TimeUnit.MINUTES)
    }

    /**
     * Envia o próximo anúncio para todos os jogadores online.
     */
    private fun sendAnnouncement() {
        val onlinePlayers = Bukkit.getOnlinePlayers()
        val announcement = OtherConfig.announcementsListAnnounce.getOrDefault(currentAnnouncementIndex, "")

        onlinePlayers.forEach { player ->
            if (!player.hasPermission("totalessentials.announce.bypass")) {
                player.sendMessage(announcement.replace("%players_online%", onlinePlayers.size.toString()))
            }
        }

        currentAnnouncementIndex = if (currentAnnouncementIndex >= maxAnnouncementIndex) 0 else currentAnnouncementIndex + 1
    }
}
