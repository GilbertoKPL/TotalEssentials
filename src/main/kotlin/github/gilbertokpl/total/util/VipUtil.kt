package github.gilbertokpl.total.util

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.local.VipData
import github.gilbertokpl.total.discord.Discord
import org.bukkit.World
import org.bukkit.entity.Player

object VipUtil {

    val world : World? = null

    fun checkVip(entity: String) {
        val vips = PlayerData.vipCache[entity] ?: return

        val toExclude = ArrayList<String>()

        for (v in vips) {
            if (v.value < System.currentTimeMillis()) {
                toExclude.add(v.key)
            }
        }

        for (t in toExclude) {
            PlayerData.vipCache.remove(entity, t)
            TotalEssentials.permission.playerRemoveGroup(world, entity, VipData.vipGroup[t])
            Discord.removeUserRole(PlayerData.discordCache[entity] ?: continue, VipData.vipDiscord[t] ?: continue)
        }

        if (toExclude.isNotEmpty()) {
            updateCargo(entity)
        }
    }

    fun updateCargo(entity: String, newVip: String? = null) : String? {

        val vips = PlayerData.vipCache[entity] ?: return null

        val size = vips.size

        val sequence = ArrayList<String>()

        var currentGroup : String? = null

        for (v in vips) {
            sequence.add(v.key)
        }

        for (g in sequence) {
            val group = VipData.vipGroup[g] ?: continue
            if (TotalEssentials.permission.playerInGroup(world, entity, group)) {
                currentGroup = g
                break
            }
        }

        if (currentGroup == null) {
            if (newVip != null) {
                TotalEssentials.permission.playerAddGroup(world, entity, newVip)
                return null
            }
            if (size > 0) {
                val newGroup = vips.keys.first()
                TotalEssentials.permission.playerAddGroup(world,entity, VipData.vipGroup[newGroup])
                return newGroup
            }
            return null
        }

        var value = 0

        for (i in sequence) {
            println(i)
            value += 1
            if (i == currentGroup) {
                break
            }
        }

        if (size < (value + 1)) {
            value = 1
        }
        else {
            value += 1
        }

        TotalEssentials.permission.playerRemoveGroup(world, entity , VipData.vipGroup[currentGroup])

        if (newVip != null) {
            TotalEssentials.permission.playerAddGroup(world,entity, newVip)
            return null
        }

        TotalEssentials.permission.playerAddGroup(world,entity, VipData.vipGroup[sequence[value - 1]])

        return sequence[value - 1]

    }

}