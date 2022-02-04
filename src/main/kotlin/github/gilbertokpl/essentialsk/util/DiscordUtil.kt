package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.api.exceptions.BotIsNotActivated
import github.gilbertokpl.essentialsk.api.exceptions.BotIsNotInitialized
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.listeners.discord.ChatDiscord
import github.gilbertokpl.essentialsk.manager.EColor
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag

internal object DiscordUtil {

    var jda: JDA? = null

    fun getJdaCheck(): JDA {
        val jda = DiscordUtil.jda

        if (jda == null && MainConfig.discordbotConnectDiscordChat) {
            MainUtil.consoleMessage(
                EColor.YELLOW.color + GeneralLang.discordchatNoToken + EColor.RESET.color
            )
            throw BotIsNotInitialized()
        }

        if (jda == null) {
            throw BotIsNotActivated()
        }

        return jda
    }

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
        } catch (e: Throwable) {
            null
        }
    }

    fun setupDiscordChat(): TextChannel? {
        val jda = getJdaCheck()
        val newChat =
            jda.getTextChannelById(MainConfig.discordbotIdDiscordChat) ?: run {
                MainUtil.consoleMessage(
                    EColor.YELLOW.color + GeneralLang.discordchatNoChatId + EColor.RESET.color
                )
                return null
            }
        return newChat
    }
}
