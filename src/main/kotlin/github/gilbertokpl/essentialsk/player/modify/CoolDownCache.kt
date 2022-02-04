package github.gilbertokpl.essentialsk.player.modify

import github.gilbertokpl.essentialsk.player.PlayerData

internal object CoolDownCache {
    fun PlayerData.setCoolDown(commandName: String, Long: Long) {
        coolDownCommand[commandName] = Long
    }

    fun PlayerData.getCoolDown(commandName: String): Long {
        return coolDownCommand[commandName] ?: 0
    }
}