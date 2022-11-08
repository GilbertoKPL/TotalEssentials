package github.gilbertokpl.total.discord

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.exceptions.*
import github.gilbertokpl.total.discord.listeners.ChatDiscord
import github.gilbertokpl.total.util.ColorUtil
import github.gilbertokpl.total.util.MainUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.UserSnowflake
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import okhttp3.internal.wait
import org.bukkit.entity.Snowman
import org.jetbrains.exposed.sql.wrapAsExpression
import java.awt.Color
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import javax.security.auth.login.LoginException

internal object Discord {

    var jda: JDA? = null

    fun randomColor(): Color = Color.getHSBColor(
        (Math.random() * 255 + 1).toFloat(),
        (Math.random() * 255 + 1).toFloat(),
        (Math.random() * 255 + 1).toFloat()
    )

    private val hashTextChannel = HashMap<String, TextChannel>()

        /**
         * Send message to discord with chat ID.
         *
         * @throws BotIsNotInitialized if bot is true but token is incorrect.
         * @throws BotIsNotActivated if bot is not activated.
         * @throws ChatDoesNotExist if chat does not exist.
         */
        fun sendDiscordMessage(message: String, embed: Boolean) {
            sendDiscordMessage(message, MainConfig.discordbotIdDiscordChat, embed)
        }

        /**
         * Send message to discord with chat ID.
         *
         * @throws BotIsNotInitialized if bot is true but token is incorrect.
         * @throws BotIsNotActivated if bot is not activated.
         * @throws ChatDoesNotExist if chat does not exist.
         */
        fun sendDiscordMessage(message: String, chatID: String, embed: Boolean) {

            if (hashTextChannel[chatID] == null) {
                TotalEssentials.basePlugin.getTask().async {
                    val newChat = setupDiscordChat() ?: throw ChatDoesNotExist()

                    hashTextChannel[chatID] = newChat

                    if (embed) {
                        val msg = EmbedBuilder().setDescription(message).setColor(randomColor())
                        newChat.sendMessageEmbeds(msg.build()).queue()
                        return@async
                    }

                    newChat.sendMessage(message).queue()

                }
                return
            }

            try {
                if (embed) {
                    val msg = EmbedBuilder().setDescription(message).setColor(randomColor())
                    hashTextChannel[chatID]!!.sendMessageEmbeds(msg.build()).queue()
                    return
                }
                hashTextChannel[chatID]!!.sendMessage(message).queue()
            } catch (e: Throwable) {
                hashTextChannel.remove(chatID)
            }
        }

        /**
         * Add role to user.
         *
         * @throws BotIsNotInitialized if bot is true but token is incorrect.
         * @throws BotIsNotActivated if bot is not activated.
         * @throws UserDoesNotExist if user does not exist.
         * @throws RoleDoesNotExist if role does not exist.
         */
        fun addUserRole(userID: Long, roleId: Long) {
            val jda = getJdaCheck()

            val role = jda.getRoleById(roleId) ?: throw RoleDoesNotExist()

            jda.getUserById(userID) ?: throw UserDoesNotExist()

            jda.getGuildById(jda.guilds[0].id)?.addRoleToMember(UserSnowflake.fromId(userID), role)
        }

        /**
         * remove user role.
         *
         * @throws BotIsNotInitialized if bot is true but token is incorrect.
         * @throws BotIsNotActivated if bot is not activated.
         * @throws UserDoesNotExist if user does not exist.
         * @throws RoleDoesNotExist if role does not exist.
         * @throws UserDoesNotHaveThisRole if user does not have this role.
         */
        fun removeUserRole(userID: Long, roleId: Long) {
            val jda = getJdaCheck()

            val role = jda.getRoleById(roleId) ?: throw RoleDoesNotExist()

            val user = jda.getUserById(userID) ?: throw UserDoesNotExist()

            if (!(user as Member).roles.contains(role)) {
                throw UserDoesNotHaveThisRole()
            }

            jda.getGuildById(jda.guilds[0].id)?.removeRoleFromMember(UserSnowflake.fromId(userID), role)
        }

        /**
         * Reload all chat ids.
         */
        fun reloadDiscordChat() {
            hashTextChannel.clear()
        }

        /**
         * Get chat id in use.
         *
         * @return chat in use, null if bot not in use.
         */
        fun getChatIdInUse(): String? {
            return hashTextChannel[MainConfig.discordbotIdDiscordChat]?.id
        }

    private fun getJdaCheck(): JDA {
        val jda = jda

        if (jda == null && MainConfig.discordbotConnectDiscordChat) {
            MainUtil.consoleMessage(
                ColorUtil.YELLOW.color + LangConfig.discordchatNoToken + ColorUtil.RESET.color
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
            JDABuilder.createLight(MainConfig.discordbotToken)
                .setAutoReconnect(true)
                .disableCache(CacheFlag.ACTIVITY)
                .setLargeThreshold(50)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(ChatDiscord())
                .build()
        } catch (e: LoginException) {
            null
        } catch (e: Throwable) {
            null
        }
    }

    private fun setupDiscordChat(): TextChannel? {
        val jda = getJdaCheck()
        val newChat =
            jda.getTextChannelById(MainConfig.discordbotIdDiscordChat) ?: run {
                MainUtil.consoleMessage(
                    ColorUtil.YELLOW.color + LangConfig.discordchatNoChatId + ColorUtil.RESET.color
                )
                return null
            }
        return newChat
    }
}