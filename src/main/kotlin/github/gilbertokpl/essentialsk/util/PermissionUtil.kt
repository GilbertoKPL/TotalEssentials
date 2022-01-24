package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.EssentialsK.Companion.lowVersion
import org.bukkit.entity.Player

internal object PermissionUtil {
    private const val MAX_HOME_VALUE = 1000

    fun getNumberPermission(player: Player, permission: String, default: Int): Int {
        var newAmount = 0
        //lower versions
        if (lowVersion) {
            for (i in 0..MAX_HOME_VALUE) {
                if (player.hasPermission(permission + i) && newAmount <= i) {
                    newAmount = i
                }
            }
            return if (newAmount == 0) {
                default
            } else {
                newAmount
            }
        }

        player.effectivePermissions.filter { it.permission.contains(permission) }.forEach {
            val int = try {
                it.permission.split(".").last().toInt()
            } catch (e: Throwable) {
                0
            }
            if (newAmount <= int) {
                newAmount = int
            }
        }
        return if (newAmount == 0) {
            default
        } else {
            newAmount
        }
    }

    fun colorPermission(p: Player?, message: String): String {
        if (!message.contains("&") && !message.contains("#")) return message

        if (p == null) {
            return ColorUtil.rgbHex(null, message)
        }

        if (p.hasPermission("essentialsk.color.*")) {
            return ColorUtil.rgbHex(p, message)
        }
        return ColorUtil.color(p, message)
    }
}
