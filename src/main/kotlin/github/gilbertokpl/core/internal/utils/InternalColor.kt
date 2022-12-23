package github.gilbertokpl.core.internal.utils

import github.gilbertokpl.core.external.CorePlugin
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import java.util.regex.Pattern

internal class InternalColor(lf: CorePlugin) {

    private val lunarFrame = lf

    private var works = true

    private val list = listOf(
        "&1", "&2", "&3", "&4", "&5", "&6", "&7",
        "&8", "&9", "&a", "&b", "&c", "&d", "&e",
        "&f", "&k", "&r", "&l", "&n"
    )

    fun rgbHex(p: Player?, strv: String): String {
        if (works) {
            try {
                var string = strv
                val pattern = Pattern.compile("#[a-fA-F0-9]{6}")
                var matcher = pattern.matcher(string)
                while (matcher.find()) {
                    val color = string.substring(matcher.start(), matcher.end())
                    string = string.replace(
                        color,
                        ChatColor.stripColor(color).toString() + ""
                    )
                    matcher = pattern.matcher(string)
                }
                string = ChatColor.translateAlternateColorCodes('&', string)
                return string
            } catch (e: ClassNotFoundException) {
                return color(p, strv)
            } catch (e: NoClassDefFoundError) {
                return color(p, strv)
            } catch (e: NoSuchMethodError) {
                return color(p, strv)
            } catch (e: Throwable) {
                return color(p, strv)
            }
        } else {
            return color(p, strv)
        }
    }

    fun color(p: Player?, strv: String): String {
        if (p == null) {
            return strv.replace("&", "§")
        }
        var newMessage = strv
        fun colorHelper(color: String) {
            newMessage = if (p.hasPermission("totalessentials.color.$color")) {
                newMessage.replace(color, color.replace("&", "§"))
            } else {
                newMessage.replace(color, "")
            }
        }
        list.forEach {
            colorHelper(it)
        }
        return newMessage
    }
}