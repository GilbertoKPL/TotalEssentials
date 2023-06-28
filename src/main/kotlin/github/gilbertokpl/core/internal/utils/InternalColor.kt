package github.gilbertokpl.core.internal.utils

import github.gilbertokpl.core.external.CorePlugin
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import java.util.regex.Pattern

internal class InternalColor(private val corePlugin: CorePlugin) {

    private val colorPattern = Pattern.compile("#[a-fA-F0-9]{6}")

    private var works = true

    private val colorPermissions = listOf(
        "&1", "&2", "&3", "&4", "&5", "&6", "&7",
        "&8", "&9", "&a", "&b", "&c", "&d", "&e",
        "&f", "&k", "&r", "&l", "&n"
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
                if (player != null) {
                    if (player.hasPermission(permissionNode)) {
                        modifiedString = modifiedString.replace(permission, permission.replace("&", "§"))
                    } else {
                        modifiedString = modifiedString.replace(permission, "")
                    }
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
            if (player.hasPermission("totalessentials.color.$permission")) {
                colorList.add("${permission.replace("&", "§")}$permission")
            }
        }
        return colorList
    }
}