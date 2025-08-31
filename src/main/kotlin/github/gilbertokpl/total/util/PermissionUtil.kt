
package github.gilbertokpl.total.util

import github.gilbertokpl.total.TotalEssentials
import org.bukkit.Bukkit
import org.bukkit.entity.Player


internal object PermissionUtil {
    private const val MAX_HOME_VALUE = 1000

    fun getNumberPermission(player: Player?, permission: String, default: Int): Int {
        if (player == null || !player.isOnline) return default

        return try {
            val maxValue = if (TotalEssentials.isLowVersion()) {
                (0..MAX_HOME_VALUE)
                    .filter { player.hasPermission("$permission$it") }
                    .maxOrNull() ?: 0
            } else {
                player.effectivePermissions
                    .asSequence()
                    .mapNotNull {
                        val perm = it.permission
                        if (perm.startsWith(permission)) perm.substringAfterLast(".").toIntOrNull() else null
                    }
                    .maxOrNull() ?: 0
            }
            if (maxValue == 0) default else maxValue
        } catch (ex: Exception) {
            Bukkit.getLogger().severe("[TotalEssentials] Error while checking permission for ${player.name}: ${ex.message}")
            default
        }
    }

    fun colorPermission(player: Player?, message: String): String {
        if (!message.contains("&") && !message.contains("#")) return message

        val colorApi = TotalEssentials.getCore().getColor()

        return when {
            player == null -> colorApi.rgbHex(null, message)
            player.hasPermission("totalessentials.color.*") -> colorApi.rgbHex(player, message)
            else -> colorApi.color(player, message)
        }
    }
}

