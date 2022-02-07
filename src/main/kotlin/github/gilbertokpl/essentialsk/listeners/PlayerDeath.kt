package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.config.otherConfigs.OtherConfig
import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.player.modify.BackCache.setBack
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.MainUtil
import org.apache.commons.lang3.exception.ExceptionUtils
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
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
        if (MainConfig.messagesDeathmessagesMessage) {
            try {
                deathMessage(e)
            } catch (e: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
        if (MainConfig.addonsPlayerPreventLoseXp) {
            try {
                loseXP(e)
            } catch (e: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun loseXP(e: PlayerDeathEvent) {
        e.keepLevel = true
        e.droppedExp = 0
    }

    private fun setBackLocation(e: PlayerDeathEvent) {
        if (!e.entity.hasPermission("essentialsk.commands.back") || MainConfig.backDisabledWorlds.contains(
                e.entity.world.name.lowercase()
            ) && !e.entity.hasPermission("essentialsk.bypass.backblockedworlds")
        ) return
        PlayerData[e.entity]?.setBack(e.entity.location) ?: return
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
