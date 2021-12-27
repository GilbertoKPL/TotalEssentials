package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.manager.IInstance
import org.bukkit.entity.Player

class PermissionUtil {

    fun getNumberPermission(player: Player, permission: String, default: Int): Int {
        for (perm in player.effectivePermissions) {
            val permString = perm.permission
            if (permString.startsWith(permission)) {
                val amount = permString.split(".").toTypedArray()
                return try {
                    amount.last().toInt()
                } catch (e: Exception) {
                    default
                }
            }
        }
        return default
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