package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.listeners.ChatDiscord
import github.gilbertokpl.essentialsk.manager.IInstance
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag

class DiscordUtil {

    var jda: JDA? = null

    fun startBot() {
        jda = try {
            JDABuilder.createDefault(MainConfig.getInstance().discordbotToken)
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


    companion object : IInstance<DiscordUtil> {
        private val instance = createInstance()
        override fun createInstance(): DiscordUtil = DiscordUtil()
        override fun getInstance(): DiscordUtil {
            return instance
        }
    }
}