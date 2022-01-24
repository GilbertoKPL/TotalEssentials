package github.gilbertokpl.essentialsk.api

import github.gilbertokpl.essentialsk.api.discord.Discord
import org.bukkit.plugin.java.JavaPlugin

class EssentialsKAPI(pl: JavaPlugin) {

    private val plugin = pl

    private val discordAPI = Discord(plugin)

    fun getDiscordAPI(): Discord {
        return discordAPI
    }
}
