package github.genesyspl.cardinal.util

import github.genesyspl.cardinal.events.DiscordChatEvent
import github.genesyspl.cardinal.manager.IInstance
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag

class DiscordUtil {

    var jda: JDA? = null

    fun startBot() {
        jda = try {
            JDABuilder.createDefault(github.genesyspl.cardinal.configs.MainConfig.getInstance().discordbotToken)
                .setAutoReconnect(true)
                .disableCache(CacheFlag.ACTIVITY)
                .setLargeThreshold(50)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(DiscordChatEvent())
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