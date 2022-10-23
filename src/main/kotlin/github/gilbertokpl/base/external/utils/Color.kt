package github.gilbertokpl.base.external.utils

import github.gilbertokpl.base.external.BasePlugin
import github.gilbertokpl.base.internal.utils.InternalColor
import org.bukkit.entity.Player

class Color(lf: BasePlugin) {
    private val colorInstance = InternalColor(lf)

    fun rgbHex(player: Player?, str: String): String {
        return colorInstance.rgbHex(player, str)
    }

    fun color(player: Player?, str: String): String {
        return colorInstance.color(player, str)
    }

}