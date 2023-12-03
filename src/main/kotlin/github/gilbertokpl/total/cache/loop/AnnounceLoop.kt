package github.gilbertokpl.total.cache.loop

import  github.gilbertokpl.total.cache.internal.InternalLoader
import github.gilbertokpl.total.util.TaskUtil
import org.bukkit.Bukkit
import java.util.concurrent.TimeUnit

internal object AnnounceLoop {
    private var currentAnnouncementIndex = 0
    private var maxAnnouncementIndex = 0

    fun start(maxAnnouncements: Int, intervalInMinutes: Int) {
        maxAnnouncementIndex = maxAnnouncements

        TaskUtil.getInternalExecutor().scheduleWithFixedDelay(
            ::sendAnnouncement,
            intervalInMinutes * 20L,
            intervalInMinutes * 20L,
            TimeUnit.MINUTES
        )
    }

    private fun sendAnnouncement() {
        val onlinePlayers = Bukkit.getOnlinePlayers()
        val announcement = InternalLoader.announcementsListAnnounce.getOrDefault(currentAnnouncementIndex, "")

        onlinePlayers.forEach { player ->
            if (!player.hasPermission("totalessentials.announce.bypass")) {
                player.sendMessage(announcement.replace("%players_online%", onlinePlayers.size.toString()))
            }
        }

        currentAnnouncementIndex =
            if (currentAnnouncementIndex >= maxAnnouncementIndex) 0 else currentAnnouncementIndex + 1
    }
}
