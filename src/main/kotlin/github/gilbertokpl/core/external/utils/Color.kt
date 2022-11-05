package github.gilbertokpl.core.external.utils

import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.core.internal.utils.InternalColor
import org.bukkit.entity.Player

class Color(lf: CorePlugin) {
    private val colorInstance = InternalColor(lf)

    fun rgbHex(player: Player?, str: String): String {
        return colorInstance.rgbHex(player, str)
    }

    fun color(player: Player?, str: String): String {
        return colorInstance.color(player, str)
    }

}