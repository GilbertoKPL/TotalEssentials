package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.internal.OtherConfig
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.MainUtil
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerDeath : Listener {
    @EventHandler
    fun event(e: PlayerDeathEvent) {
        if (MainConfig.backActivated) {
            try {
                setBackLocation(e)
            } catch (e: Throwable) {

            }
        }
        if (MainConfig.messagesDeathmessagesMessage) {
            try {
                deathMessage(e)
            } catch (e: Throwable) {

            }
        }
        if (MainConfig.addonsPlayerPreventLoseXp) {
            try {
                loseXP(e)
            } catch (e: Throwable) {

            }
        }
    }

    private fun loseXP(e: PlayerDeathEvent) {
        e.keepLevel = true
        e.droppedExp = 0
    }

    private fun setBackLocation(e: PlayerDeathEvent) {
        if (!e.entity.hasPermission("totalessentials.commands.back") || MainConfig.backDisabledWorlds.contains(
                e.entity.world.name.lowercase()
            ) && !e.entity.hasPermission("totalessentials.bypass.backblockedworlds")
        ) return
        PlayerData.backLocation[e.entity] = e.entity.location
    }

    private fun deathMessage(e: PlayerDeathEvent) {
        e.deathMessage = null

        val pName = e.entity.player!!.name

        val damageCause = e.entity.player!!.lastDamageCause ?: run {
            MainUtil.serverMessage(
                LangConfig.deathmessagesNothingKillPlayer
                    .replace("%player%", pName)
            )
            return
        }

        if (damageCause.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            val ent = damageCause as EntityDamageByEntityEvent
            val dmg = ent.damager
            if (dmg is Player) {
                MainUtil.serverMessage(
                    LangConfig.deathmessagesPlayerKillPlayer
                        .replace("%player%", pName)
                        .replace("%killer%", dmg.name)
                )
                return
            }
            val causeMessage =
                OtherConfig.deathmessageListReplacer[ent.damager.toString().lowercase()] ?: run {
                    ent.damager.toString().lowercase()
                }
            MainUtil.serverMessage(
                LangConfig.deathmessagesEntityKillPlayer
                    .replace("%player%", pName)
                    .replace("%entity%", causeMessage)
            )
            return
        }

        val causeMessage =
            OtherConfig.deathmessageListReplacer[damageCause.cause.name.lowercase()] ?: run {
                MainUtil.consoleMessage(
                    LangConfig.deathmessagesCauseNotExist
                        .replace("%cause%", damageCause.cause.name.lowercase())
                )
                MainUtil.serverMessage(
                    LangConfig.deathmessagesNothingKillPlayer
                        .replace("%player%", pName)
                )
                return
            }
        MainUtil.serverMessage(
            causeMessage.replace("%player%", pName)
        )
    }
}
