package github.gilbertokpl.core.utils

import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import java.util.regex.Pattern

class ColorUtil() {
    private val colorPattern = Pattern.compile("#[a-fA-F0-9]{6}")

    private var works = true

    private val colorPermissions = listOf(
        "&1", "&2", "&3", "&4", "&5", "&6", "&7",
        "&8", "&9", "&a", "&b", "&c", "&d", "&e",
        "&f", "&g", "&k", "&r", "&l", "&n"
    )

    fun rgbHex(player: Player?, string: String): String {
        if (works) {
            return try {
                var modifiedString = string
                var matcher = colorPattern.matcher(modifiedString)
                while (matcher.find()) {
                    val color = modifiedString.substring(matcher.start(), matcher.end())
                    modifiedString = modifiedString.replace(color, ChatColor.stripColor(color).toString() + "")
                    matcher = colorPattern.matcher(modifiedString)
                }
                modifiedString = ChatColor.translateAlternateColorCodes('&', modifiedString)
                    .replace("&g", "\u00A7g", ignoreCase = true)
                modifiedString
            } catch (e: Throwable) {
                works = false
                color(player, string)
            }
        } else {
            return color(player, string)
        }
    }

    fun color(player: Player?, string: String): String {
        val hasPlayer = player != null
        var modifiedString = string
        colorPermissions.forEach { permission ->
            if (hasPlayer) {
                val permissionNode = "totalessentials.color.$permission"
                modifiedString = if (hasPermission(player, permissionNode)) {
                    modifiedString.replace(permission, permission.replace("&", "§"))
                } else {
                    modifiedString.replace(permission, "")
                }
            } else {
                modifiedString = modifiedString.replace(permission, permission.replace("&", "§"))
            }
        }
        return modifiedString
    }

    fun list(player: Player): List<String> {
        val colorList = ArrayList<String>()
        colorPermissions.forEach { permission ->
            if (hasPermission(player, "totalessentials.color.$permission")) {
                colorList.add("${permission.replace("&", "§")}$permission")
            }
        }
        return colorList
    }

    private fun hasPermission(player: Player, permission: String): Boolean {
        return try {
            player.hasPermission(permission)
        } catch (_: Throwable) {
            false
        }
    }

}
