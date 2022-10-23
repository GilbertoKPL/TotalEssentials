package github.gilbertokpl.total.util

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.PlayerData
import org.bukkit.GameMode
import org.bukkit.entity.Player


internal object PlayerUtil {

    fun sendMessage(player: String, message: String) {
        val p = github.gilbertokpl.total.TotalEssentials.instance.server.getPlayerExact(player.lowercase()) ?: return
        p.sendMessage(message)
    }

    fun getIntOnlinePlayers(vanish: Boolean): Int {
        var amount = github.gilbertokpl.total.TotalEssentials.basePlugin.getReflection().getPlayers()
        if (!vanish) {
            amount = amount.filter {
                PlayerData.vanishCache[it] != null && !PlayerData.vanishCache[it]!!
            }
        }
        return amount.size
    }

    fun getNumberGamemode(gamemode: GameMode): Int {
        try {
            if (gamemode == GameMode.SURVIVAL) {
                return 0
            }
            if (gamemode == GameMode.CREATIVE) {
                return 1
            }
            if (gamemode == GameMode.ADVENTURE) {
                return 2
            }
            if (gamemode == GameMode.SPECTATOR) {
                return 3
            }
        } catch (e: Throwable) {
            if (gamemode == GameMode.SURVIVAL) {
                return 0
            }
            if (gamemode == GameMode.CREATIVE) {
                return 1
            }
        }
        return 1
    }

    fun getGamemodeNumber(number: String): GameMode {
        return when (number.lowercase()) {
            "0" -> GameMode.SURVIVAL
            "1" -> GameMode.CREATIVE
            "survival" -> GameMode.SURVIVAL
            "creative" -> GameMode.CREATIVE
            "adventure" -> try {
                GameMode.ADVENTURE
            } catch (e: NoSuchMethodError) {
                GameMode.SURVIVAL
            }

            "spectactor" -> try {
                GameMode.SPECTATOR
            } catch (e: NoSuchMethodError) {
                GameMode.SURVIVAL
            }

            "2" -> try {
                GameMode.ADVENTURE
            } catch (e: NoSuchMethodError) {
                GameMode.SURVIVAL
            }

            "3" -> try {
                GameMode.SPECTATOR
            } catch (e: NoSuchMethodError) {
                GameMode.SURVIVAL
            }

            else -> GameMode.SURVIVAL
        }
    }

    fun finishLogin(p: Player, vanishCache: Boolean) {

        if (!vanishCache && !p.hasPermission("*")) {
            if (MainConfig.messagesLoginMessage) {
                MainUtil.serverMessage(
                    LangConfig.messagesEnterMessage
                        .replace("%player%", p.name)
                )
            }
            if (MainConfig.discordbotSendLoginMessage) {
                //discord
            }
        }

        if (MainConfig.vanishActivated) {
            if (p.hasPermission("totalessentials.commands.vanish") ||
                p.hasPermission("totalessentials.bypass.vanish")
            ) return
            for (it in github.gilbertokpl.total.TotalEssentials.basePlugin.getReflection().getPlayers()) {
                if (PlayerData.vanishCache[it] ?: continue) {
                    @Suppress("DEPRECATION")
                    p.hidePlayer(it)
                }
            }
        }
    }
}
