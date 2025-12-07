package github.gilbertokpl.total.discord

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import github.gilbertokpl.core.utils.ConsoleColorUtil
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.exceptions.*
import github.gilbertokpl.total.discord.listeners.ChatDiscordEvent
import github.gilbertokpl.total.util.ServerUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.exceptions.ContextException
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.awt.Color
import javax.security.auth.login.LoginException

internal object DiscordManager {

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
     * @throws ChatDoesNotExist if chat does not exist.
     */
    fun sendDiscordMessage(message: String, embed: Boolean = false, tittle: Boolean = false, avatarUrl: String? = null) {
        if (!embed) {
            sendDiscordMessage(message, MainConfig.discordbotIdDiscordChat, false)
        }
        if (tittle) {
            sendDiscordMessage(message, MainConfig.discordbotIdDiscordChat, LangConfig.discordchatFooter.replace("%time%", ServerUtil.getCurrentTime()), avatarUrl)
            return
        }
    }

    /**
     * Send message to discord with chat ID.
     *
     * @throws BotIsNotInitialized if bot is true but token is incorrect.
     * @throws ChatDoesNotExist if chat does not exist.
     */
    fun sendDiscordMessage(message: String, chatID: String, embed: Boolean) {
        if (!MainConfig.discordbotConnectDiscordChat) {
            return
        }

        if (hashTextChannel[chatID] == null) {
            TotalEssentials.getCore().getTask().async {
                val newChat = setupDiscordChat(chatID) ?: throw ChatDoesNotExist()

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


    fun sendDiscordMessage(userID: Long, message: String): Boolean {

        if (!MainConfig.discordbotConnectDiscordChat) {
            return false
        }

        val jda = getJdaCheck()

        try {
            jda.retrieveUserById(userID).complete()?.openPrivateChannel()?.queue { channel ->
                channel.sendMessage(message).queue()
            } ?: return false
        } catch (e: ContextException) {
            return false
        } catch (e: Exception) {
            return false
        }

        return true
    }

    fun sendPlayerWebhook(playerName: String, avatarUrl: String?, message: String) {
        val client = WebhookClient.withUrl(MainConfig.discordbotIdWebhookChat)

        val msg = WebhookMessageBuilder()
            .setUsername(playerName)
            .setAvatarUrl(avatarUrl)
            .setContent(message)         // Mensagem sem embed
            .build()

        client.send(msg)
        client.close()
    }

    private fun sendDiscordMessage(message: String, chatID: String, footer: String, avatarUrl: String? = null) {
        if (!MainConfig.discordbotConnectDiscordChat) return

        val channel = hashTextChannel[chatID]

        TotalEssentials.getCore().getTask().async {
            val sendEmbed = { ch: TextChannel ->
                val embed = EmbedBuilder()
                    .setFooter(footer)
                    .setColor(randomColor())
                    .apply {
                        if (avatarUrl != null)
                            setAuthor(message, null, avatarUrl)
                        else {
                            setDescription("**$message**")
                        }
                    }
                    .build()

                ch.sendMessageEmbeds(embed).queue()
            }
            if (channel == null) {
                val newChat = setupDiscordChat(chatID) ?: throw ChatDoesNotExist()
                hashTextChannel[chatID] = newChat
                sendEmbed(newChat)
                return@async
            }

            try {
                sendEmbed(channel)
            } catch (e: Throwable) {
                hashTextChannel.remove(chatID)
            }
        }
    }

    fun checkIfRoleIdExist(roleId: Long): Boolean {

        if (!MainConfig.discordbotConnectDiscordChat) {
            return false
        }

        val jda = getJdaCheck()

        jda.getRoleById(roleId) ?: return false

        return true
    }


    /**
     * Add role to user.
     *
     * @throws BotIsNotInitialized if bot is true but token is incorrect.
     * @throws UserDoesNotExist if user does not exist.
     * @throws RoleDoesNotExist if role does not exist.
     */
    fun addUserRole(userID: Long, roleId: Long): Boolean {

        if (!MainConfig.discordbotConnectDiscordChat) {
            return false
        }

        val jda = getJdaCheck()

        try {
            val role = jda.getRoleById(roleId) ?: throw RoleDoesNotExist()
            jda.retrieveUserById(userID).queue {
                jda.getGuildById(jda.guilds[0].id)?.addRoleToMember(it, role)?.queue()
            }
        } catch (e: ContextException) {
            return false
        } catch (e: Exception) {
            return false
        } catch (e: ErrorResponseException) {
            return false
        } catch (e: Throwable) {
            return false
        }

        return true
    }

    /**
     * remove user role.
     *
     * @throws BotIsNotInitialized if bot is true but token is incorrect.
     * @throws UserDoesNotExist if user does not exist.
     * @throws RoleDoesNotExist if role does not exist.
     * @throws UserDoesNotHaveThisRole if user does not have this role.
     */
    fun removeUserRole(userID: Long, roleId: Long): Boolean {

        if (!MainConfig.discordbotConnectDiscordChat) {
            return false
        }

        val jda = getJdaCheck()

        try {
            val role = jda.getRoleById(roleId) ?: return false
            jda.retrieveUserById(userID).queue {
                jda.getGuildById(jda.guilds[0].id)?.removeRoleFromMember(it, role)?.queue()
            }
        } catch (e: ContextException) {
            return false
        } catch (e: Exception) {
            return false
        }

        return true
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
            ServerUtil.consoleMessage(
                ConsoleColorUtil.YELLOW.color + LangConfig.discordchatNoToken + ConsoleColorUtil.RESET.color
            )
            throw BotIsNotInitialized()
        }

        if (jda == null) {
            Bukkit.getServer().shutdown()
            throw BotIsNotInitialized()
        }

        return jda
    }

    fun startBot() {
        jda = try {
            JDABuilder.createLight(MainConfig.discordbotToken)
                .setAutoReconnect(true)
                .setLargeThreshold(50)
                .disableCache(CacheFlag.ACTIVITY)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(ChatDiscordEvent())
                .build()
                .awaitReady()
        } catch (e: LoginException) {
            println(e)
            null
        } catch (e: Throwable) {
            println(e)
            null
        }
    }

    private fun setupDiscordChat(chatID: String): TextChannel? {

        val jda = getJdaCheck()

        val newChat =
            jda.getTextChannelById(chatID) ?: run {
                ServerUtil.consoleMessage(
                    ConsoleColorUtil.YELLOW.color + LangConfig.discordchatNoChatId + ConsoleColorUtil.RESET.color
                )
                return null
            }
        return newChat
    }
}