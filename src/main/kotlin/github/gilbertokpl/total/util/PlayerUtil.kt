package github.gilbertokpl.total.util

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.local.ShopData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.apache.commons.io.IOUtils
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.json.JSONObject
import java.net.URL


internal object PlayerUtil {

    fun sendAllMessage(message: String) {
        for (p in TotalEssentials.basePlugin.getReflection().getPlayers()) {
            p.sendMessage(message)
        }
    }

    fun sendMessage(player: String, message: String) {
        val p = TotalEssentials.instance.server.getPlayerExact(player.lowercase()) ?: return
        p.sendMessage(message)
    }

    fun getIntOnlinePlayers(vanish: Boolean): Int {
        var amount = TotalEssentials.basePlugin.getReflection().getPlayers()
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

    fun checkPlayer(address: String): List<String> {
        if (address.contains("127.0.0.1")) return listOf(
            "País: Local",
            "Estado: Local",
            "Cidade: Local",
            "false"
        )
        val result = JSONObject(
            IOUtils.toString(
                URL("http://ip-api.com/json$address?fields=status,country,regionName,city,hosting,query"),
                "UTF-8"
            )
        )
        if (!(result["status"]?.equals("fail"))!!) {
            return listOf(
                "País: ${result.get("country")}",
                "Estado: ${result.get("regionName")}",
                "Cidade: ${result.get("city")}",
                "${result.get("hosting")}"
            )
        }
        return listOf("Erro API").toList()
    }

    fun shopTeleport(p: Player, shop: String) {
        //teleport

        val time = MainConfig.homesTimeToTeleport

        if (p.hasPermission("totalessentials.bypass.teleport") || time == 0) {
            p.teleport(ShopData.shopLocation[shop]!!)
            p.sendMessage(LangConfig.shopTeleport.replace("%player%", shop))
            return
        }

        val inTeleport = PlayerData.inTeleport[p]

        if (inTeleport != null && inTeleport) {
            p.sendMessage(LangConfig.homesInTeleport)
            return
        }

        val exe = TaskUtil.teleportExecutor(time)

        PlayerData.inTeleport[p] = true

        exe {
            PlayerData.inTeleport[p] = false
            p.teleport(ShopData.shopLocation[shop] ?: return@exe)
            p.sendMessage(LangConfig.shopTeleport.replace("%player%", shop))
        }

        p.sendMessage(
            LangConfig.shopTimeToTeleport.replace("%time%", time.toString())
        )
    }

}
