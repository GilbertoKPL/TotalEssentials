package github.gilbertokpl.total.util

import github.gilbertokpl.total.TotalEssentialsJava
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.local.VipData
import github.gilbertokpl.total.discord.Discord
import net.milkbowl.vault.permission.Permission
import org.bukkit.World

object VipUtil {

    // Optional world context
    val world: World? = null

    // =========================================================
    // Check expired VIPs and remove them
    // =========================================================
    fun checkVip(entity: String): Boolean {
        val vips = PlayerData.vipCache[entity] ?: return false
        val toRemove = mutableListOf<String>()
        var hasExpired = false

        // Collect expired VIP keys
        for ((vipKey, expiry) in vips) {
            if (expiry < System.currentTimeMillis()) {
                hasExpired = true
                toRemove.add(vipKey)
            }
        }

        val perm = TotalEssentialsJava.getPermission()

        // Remove expired VIPs
        for (vipKey in toRemove) {
            PlayerData.vipCache.remove(entity, vipKey)

            if (perm is Permission) {
                perm.playerRemoveGroup(world, entity, VipData.vipGroup[vipKey])
            } else if (perm is net.milkbowl.vault2.permission.Permission) {
                perm.playerRemoveGroup(world, entity, VipData.vipGroup[vipKey])
            }

            VipData.vipQuantity[vipKey] = (VipData.vipQuantity[vipKey] ?: 0) - 1
            val token = PlayerData.discordCache[entity] ?: continue
            val roleID = VipData.vipDiscord[vipKey] ?: continue
            Discord.removeUserRole(token, roleID)
        }

        if (toRemove.isNotEmpty()) {
            updateCargo(entity)
        }

        return hasExpired
    }

    // =========================================================
    // Update VIP group for player
    // =========================================================
    fun updateCargo(entity: String, newVip: String? = null, execute: Boolean = true): String? {
        val vips = PlayerData.vipCache[entity] ?: return null
        val sequence = vips.keys.toList()
        val size = sequence.size

        val perm = TotalEssentialsJava.getPermission()
        var currentGroup: String? = null

        // Check current VIP group
        for (vipKey in sequence) {
            val group = VipData.vipGroup[vipKey] ?: continue
            if ((perm as? Permission)?.playerInGroup(world, entity, group) == true ||
                (perm as? net.milkbowl.vault2.permission.Permission)?.playerInGroup(world, entity, group) == true) {
                currentGroup = vipKey
                break
            }
        }

        // No current group assigned
        if (currentGroup == null) {
            if (newVip != null) {
                addVip(entity, newVip, perm, execute)
                return null
            } else if (size > 0) {
                val firstGroup = sequence.first()
                addVip(entity, firstGroup, perm, execute = false)
                return firstGroup
            }
            return null
        }

        // Determine next group in sequence
        var value = sequence.indexOf(currentGroup) + 1
        if (size < (value + 1)) value = 1

        removeVipGroup(entity, currentGroup, perm)

        // Assign new VIP if provided
        if (newVip != null) {
            addVip(entity, newVip, perm, execute)
            return null
        }

        // Assign next VIP in sequence
        val nextVip = sequence[value - 1]
        addVip(entity, nextVip, perm, execute = false)
        return nextVip
    }

    // =========================================================
    // Add VIP group and execute commands/roles
    // =========================================================
    private fun addVip(entity: String, vipKey: String, perm: Any?, execute: Boolean) {
        val group = VipData.vipGroup[vipKey] ?: return

        if (perm is Permission) perm.playerAddGroup(world, entity, group)
        else if (perm is net.milkbowl.vault2.permission.Permission) perm.playerAddGroup(world, entity, group)

        if (execute) {
            for (command in VipData.vipCommands[vipKey] ?: emptyList()) {
                TotalEssentialsJava.getInstance().server.dispatchCommand(
                    TotalEssentialsJava.getInstance().server.consoleSender,
                    command.replace("%player%", entity)
                )
            }
        }

        VipData.vipQuantity[vipKey] = (VipData.vipQuantity[vipKey] ?: 0) + 1

        val token = PlayerData.discordCache[entity]
        val roleID = VipData.vipDiscord[vipKey]
        if (token != null && roleID != null && token != 0L) {
            Discord.addUserRole(token, roleID)
        }
    }

    // =========================================================
    // Remove VIP group from player
    // =========================================================
    private fun removeVipGroup(entity: String, vipKey: String, perm: Any?) {
        val group = VipData.vipGroup[vipKey] ?: return
        if (perm is Permission) perm.playerRemoveGroup(world, entity, group)
        else if (perm is net.milkbowl.vault2.permission.Permission) perm.playerRemoveGroup(world, entity, group)
    }
}
