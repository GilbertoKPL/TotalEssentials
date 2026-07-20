package github.gilbertokpl.total.placeholder

import github.gilbertokpl.total.TotalEssentials
import org.bukkit.entity.Player
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * Resolves PlaceholderAPI without linking the chat path to one specific API
 * signature. Older PlaceholderAPI builds used Player while newer builds use
 * OfflinePlayer, and TotalEssentials supports both generations.
 */
object ChatPlaceholderSupport {

    @Volatile
    private var searched = false

    @Volatile
    private var setPlaceholdersMethod: Method? = null

    fun apply(player: Player, text: String): String {
        if (!text.contains('%')) return text
        if (TotalEssentials.getInstance().server.pluginManager.getPlugin("PlaceholderAPI") == null) return text

        val method = resolveMethod(player) ?: return text
        return try {
            method.invoke(null, player, text) as? String ?: text
        } catch (_: Throwable) {
            text
        }
    }

    private fun resolveMethod(player: Player): Method? {
        if (searched) return setPlaceholdersMethod

        synchronized(this) {
            if (searched) return setPlaceholdersMethod

            setPlaceholdersMethod = try {
                Class.forName("me.clip.placeholderapi.PlaceholderAPI")
                    .methods
                    .firstOrNull { method ->
                        method.name == "setPlaceholders" &&
                            Modifier.isStatic(method.modifiers) &&
                            method.parameterTypes.size == 2 &&
                            method.parameterTypes[0].isAssignableFrom(player.javaClass) &&
                            method.parameterTypes[1] == String::class.java &&
                            method.returnType == String::class.java
                    }
            } catch (_: Throwable) {
                null
            }
            searched = true
            return setPlaceholdersMethod
        }
    }
}
