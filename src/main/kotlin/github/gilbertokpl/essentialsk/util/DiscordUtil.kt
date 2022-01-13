package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.listeners.discord.ChatDiscord
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag

object DiscordUtil {

    var jda: JDA? = null

    fun startBot() {
        jda = try {
            JDABuilder.createDefault(MainConfig.discordbotToken)
                .setAutoReconnect(true)
                .disableCache(CacheFlag.ACTIVITY)
                .setLargeThreshold(50)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(ChatDiscord())
                .build()
        } catch (e: javax.security.auth.login.LoginException) {
            null
        } catch (e: Exception) {
            null
        }
    }
}
