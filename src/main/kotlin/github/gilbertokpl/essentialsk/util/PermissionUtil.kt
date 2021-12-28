package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.manager.IInstance
import org.bukkit.entity.Player

class PermissionUtil {

    fun getNumberPermission(player: Player, permission: String, default: Int): Int {
        var newAmount = 0
        for (perm in player.effectivePermissions) {
            val permString = perm.permission
            println(permString)
            if (permString.startsWith(permission)) {
                val amount = permString.split(".").toTypedArray()
                newAmount = try {
                    println(newAmount)
                    if (newAmount <= amount.last().toInt()) {
                        amount.last().toInt()
                    }
                    else {
                        continue
                    }
                } catch (e: Exception) {
                    continue
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