package github.gilbertokpl.total.util

import github.gilbertokpl.total.TotalEssentialsJava
import org.bukkit.entity.Player

internal object PermissionUtil {
    private const val MAX_HOME_VALUE = 1000

    fun getNumberPermission(player: Player, permission: String, default: Int): Int {
        var newAmount = 0

        if (TotalEssentialsJava.isLowVersion()) {
            for (i in 0..MAX_HOME_VALUE) {
                if (player.hasPermission(permission + i) && newAmount <= i) {
                    newAmount = i
                }
            }
        } else {
            player.effectivePermissions
                .filter { it.permission.contains(permission) }
                .forEach {
                    val int = try {
                        it.permission.split(".").last().toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                    newAmount = maxOf(newAmount, int)
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
            return TotalEssentialsJava.getBasePlugin().getColor().rgbHex(null, message)
        }

        if (p.hasPermission("totalessentials.color.*")) {
            return TotalEssentialsJava.getBasePlugin().getColor().rgbHex(p, message)
        }
        return TotalEssentialsJava.getBasePlugin().getColor().color(p, message)
    }
}
