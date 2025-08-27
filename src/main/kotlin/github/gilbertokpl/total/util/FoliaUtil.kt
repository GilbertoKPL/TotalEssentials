package github.gilbertokpl.total.util

import org.bukkit.Location
import org.bukkit.entity.Player
import java.lang.reflect.Method

object FoliaUtil {

    private var teleportAsyncMethod: Method? = null
    private var hasCheckedTeleport = false
    private var folia = false

    fun Player.teleportSafe(location: Location) {
        if (!hasCheckedTeleport) {
            teleportAsyncMethod = try {
                this.javaClass.getMethod("teleportAsync", Location::class.java)
            } catch (_: Exception) {
                null
            }
            hasCheckedTeleport = true
        }

        teleportAsyncMethod?.invoke(this, location) ?: run {
            this.teleport(location)
        }
    }
}

