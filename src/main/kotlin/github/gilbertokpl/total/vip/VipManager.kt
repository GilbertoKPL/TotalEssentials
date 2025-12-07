package github.gilbertokpl.total.vip

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.cache.data.VipData
import github.gilbertokpl.total.discord.DiscordManager
import net.milkbowl.vault2.permission.Permission
import org.bukkit.World

object VipManager {

    val world: World? = null

    // =========================================================
    // Detect Permission plugin once
    // =========================================================
    private val permissionInstance: Any? by lazy {
        val perm = TotalEssentials.getPermission() ?: return@lazy null

        return@lazy when (perm) {
            is net.milkbowl.vault.permission.Permission -> perm
            is Permission -> perm
            else -> null
        }
    }

    // =========================================================
    // Check expired VIPs and remove them
    // =========================================================
    fun checkVip(entity: String): Boolean {
        val vips = PlayerData.vipCache[entity] ?: return false
        val toRemove = mutableListOf<String>()
        var hasExpired = false

        for ((vipKey, expiry) in vips) {
            if (expiry < System.currentTimeMillis()) {
                hasExpired = true
                toRemove.add(vipKey)
            }
        }

        val perm = permissionInstance

        for (vipKey in toRemove) {
            PlayerData.vipCache.remove(entity, vipKey)

            when (perm) {
                is net.milkbowl.vault.permission.Permission -> perm.playerRemoveGroup(
                    world,
                    entity,
                    VipData.vipGroup[vipKey]
                )

                is Permission -> perm.playerRemoveGroup(
                    world,
                    entity,
                    VipData.vipGroup[vipKey]
                )
            }

            VipData.vipQuantity[vipKey] = (VipData.vipQuantity[vipKey] ?: 0) - 1

            val token = PlayerData.discordCache[entity] ?: continue
            val roleID = VipData.vipDiscord[vipKey] ?: continue
            DiscordManager.removeUserRole(token, roleID)
        }

        if (toRemove.isNotEmpty()) updateCargo(entity)

        return hasExpired
    }

    // =========================================================
    // Update VIP group for player
    // =========================================================
    fun updateCargo(entity: String, newVip: String? = null, execute: Boolean = true): String? {
        val vips = PlayerData.vipCache[entity] ?: return null
        val sequence = vips.keys.toList()
        val size = sequence.size
        if (size == 0) return null

        val perm = permissionInstance
        var currentGroup: String? = null

        for (vipKey in sequence) {
            val group = VipData.vipGroup[vipKey] ?: continue
            val inGroup = when (perm) {
                is net.milkbowl.vault.permission.Permission -> perm.playerInGroup(world, entity, group)
                is Permission -> perm.playerInGroup(world, entity, group)
                else -> false
            }
            if (inGroup) {
                currentGroup = vipKey
                break
            }
        }

        if (currentGroup == null) {
            if (newVip != null) {
                addVip(entity, newVip, perm, execute)
                return null
            } else {
                val firstGroup = sequence.first()
                addVip(entity, firstGroup, perm, execute = false)
                return firstGroup
            }
        }

        // =========================================================
        // Determine next VIP in circular sequence
        // =========================================================
        val currentIndex = sequence.indexOf(currentGroup)
        val nextIndex = (currentIndex + 1) % size
        val nextVip = newVip ?: sequence[nextIndex]

        removeVipGroup(entity, currentGroup, perm)
        addVip(entity, nextVip, perm, execute && newVip != null)

        return if (newVip != null) null else nextVip
    }

    // =========================================================
    // Add VIP group and execute commands/roles
    // =========================================================
    private fun addVip(entity: String, vipKey: String, perm: Any?, execute: Boolean) {
        val group = VipData.vipGroup[vipKey] ?: return

        when (perm) {
            is net.milkbowl.vault.permission.Permission -> perm.playerAddGroup(world, entity, group)
            is Permission -> perm.playerAddGroup(world, entity, group)
        }

        if (execute) {
            for (command in VipData.vipCommands[vipKey] ?: emptyList()) {
                TotalEssentials.getInstance().server.dispatchCommand(
                    TotalEssentials.getInstance().server.consoleSender,
                    command.replace("%player%", entity)
                )
            }
        }

        VipData.vipQuantity[vipKey] = ((VipData.vipQuantity[vipKey] ?: 0) + 1)

        val token = PlayerData.discordCache[entity]
        val roleID = VipData.vipDiscord[vipKey]
        if (token != null && roleID != null && token != 0L) DiscordManager.addUserRole(token, roleID)
    }

    // =========================================================
    // Remove VIP group from player
    // =========================================================
    private fun removeVipGroup(entity: String, vipKey: String, perm: Any?) {
        val group = VipData.vipGroup[vipKey] ?: return
        when (perm) {
            is net.milkbowl.vault.permission.Permission -> perm.playerRemoveGroup(world, entity, group)
            is Permission -> perm.playerRemoveGroup(world, entity, group)
        }
    }
}