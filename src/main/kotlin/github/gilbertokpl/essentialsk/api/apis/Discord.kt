package github.gilbertokpl.essentialsk.api.apis

import github.gilbertokpl.essentialsk.api.exceptions.*
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager.hashTextChannel
import github.gilbertokpl.essentialsk.util.ColorUtil
import github.gilbertokpl.essentialsk.util.DiscordUtil
import github.okkero.skedule.BukkitDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import org.bukkit.plugin.java.JavaPlugin

class Discord(pl: JavaPlugin) {

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
            CoroutineScope(BukkitDispatcher(async = true)).launch {
                val newChat = DiscordUtil.setupDiscordChat() ?: throw ChatDoesNotExist()

                hashTextChannel[chatID] = newChat

                if (embed) {
                    val msg = EmbedBuilder().setDescription(message).setColor(ColorUtil.randomColor())
                    newChat.sendMessageEmbeds(msg.build()).queue()
                    return@launch
                }

                newChat.sendMessage(message).queue()

            }
            return
        }

        try {
            if (embed) {
                val msg = EmbedBuilder().setDescription(message).setColor(ColorUtil.randomColor())
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
        val jda = DiscordUtil.getJdaCheck()

        val role = jda.getRoleById(roleId) ?: throw RoleDoesNotExist()

        jda.getUserById(userID) ?: throw UserDoesNotExist()

        jda.getGuildById(jda.guilds[0].id)?.addRoleToMember(userID, role)
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
        val jda = DiscordUtil.getJdaCheck()

        val role = jda.getRoleById(roleId) ?: throw RoleDoesNotExist()

        val user = jda.getUserById(userID) ?: throw UserDoesNotExist()

        if (!(user as Member).roles.contains(role)) {
            throw UserDoesNotHaveThisRole()
        }



        jda.getGuildById(jda.guilds[0].id)?.removeRoleFromMember(userID, role)
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
}