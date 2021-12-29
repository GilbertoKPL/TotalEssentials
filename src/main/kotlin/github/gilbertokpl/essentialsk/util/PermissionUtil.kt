package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.manager.IInstance
import org.bukkit.entity.Player

class PermissionUtil {

    fun getNumberPermission(player: Player, permission: String, default: Int): Int {
        var newAmount = 0
        for (i in 0..200) {
            if (player.hasPermission(permission + i)) {
                if (newAmount <= i) {
                    newAmount = i
                }
            }
        }
        return if (newAmount == 0) {
            default
        } else {
            newAmount
        }
    }

    fun colorPermission(p: Player, message: String): String {
        if (!message.contains("&")) return message
        var newMessage = message
        fun colorHelper(color: String) {
            if (p.hasPermission("essentialsk.color.$color")) {
                newMessage = newMessage.replace(color, color.replace("&", "§"))
            }
        }
        listOf("&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f", "&k", "&r", "&l", "&n").forEach {
            colorHelper(it)
        }
        return newMessage
    }

    companion object : IInstance<PermissionUtil> {
        private val instance = createInstance()
        override fun createInstance(): PermissionUtil = PermissionUtil()
        override fun getInstance(): PermissionUtil {
            return instance
        }
    }
}