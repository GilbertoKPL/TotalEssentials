package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.cache.internal.InternalLoader
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.ServerUtil
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerDeath : Listener {

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (MainConfig.backActivated) {
            updateBackLocation(event)
        }

        if (MainConfig.messagesDeathmessagesMessage) {
            handleDeathMessage(event)
        }

        if (MainConfig.addonsPlayerPreventLoseXp) {
            preventXpLoss(event)
        }
    }

    private fun preventXpLoss(event: PlayerDeathEvent) {
        event.keepLevel = true
        event.droppedExp = 0
    }

    private fun updateBackLocation(event: PlayerDeathEvent) {
        val player = event.entity

        if (!player.hasPermission("totalessentials.commands.back")) return

        val isWorldBlocked = MainConfig.backDisabledWorlds.contains(player.world.name.lowercase())
        val canBypassBlockedWorlds = player.hasPermission("totalessentials.bypass.backblockedworlds")

        if (isWorldBlocked && !canBypassBlockedWorlds) return

        PlayerData.backLocation[player] = player.location
    }

    private fun handleDeathMessage(event: PlayerDeathEvent) {
        event.deathMessage = null

        val player = event.entity
        val playerName = player.name
        val damageCause = player.lastDamageCause

        if (damageCause == null) {
            sendGenericDeathMessage(playerName)
            return
        }

        when (damageCause.cause) {
            EntityDamageEvent.DamageCause.ENTITY_ATTACK -> {
                handleEntityAttackDeath(damageCause, playerName)
            }
            else -> {
                handleEnvironmentalDeath(damageCause, playerName)
            }
        }
    }

    private fun handleEntityAttackDeath(damageCause: EntityDamageEvent, playerName: String) {
        val damageEvent = damageCause as? EntityDamageByEntityEvent ?: return
        val damager = damageEvent.damager

        if (damager is Player) {
            ServerUtil.broadcastMessage(
                LangConfig.deathmessagesPlayerKillPlayer
                    .replace("%player%", playerName)
                    .replace("%killer%", damager.name)
            )
            return
        }

        val entityName = InternalLoader.deathMessageListReplacer[damager.type.name.lowercase()]
            ?: damager.type.name.lowercase()

        ServerUtil.broadcastMessage(
            LangConfig.deathmessagesEntityKillPlayer
                .replace("%player%", playerName)
                .replace("%entity%", entityName)
        )
    }

    private fun handleEnvironmentalDeath(damageCause: EntityDamageEvent, playerName: String) {
        val causeName = damageCause.cause.name.lowercase()
        val causeMessage = InternalLoader.deathMessageListReplacer[causeName]

        if (causeMessage == null) {
            ServerUtil.consoleMessage(
                LangConfig.deathmessagesCauseNotExist.replace("%cause%", causeName)
            )
            sendGenericDeathMessage(playerName)
            return
        }

        ServerUtil.broadcastMessage(causeMessage.replace("%player%", playerName))
    }

    private fun sendGenericDeathMessage(playerName: String) {
        ServerUtil.broadcastMessage(
            LangConfig.deathmessagesNothingKillPlayer.replace("%player%", playerName)
        )
    }
}