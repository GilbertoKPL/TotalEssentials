package github.gilbertokpl.total.chat

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.ChatConfig
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.economy.MagnataManager
import github.gilbertokpl.total.login.VelocityAuthBridge
import github.gilbertokpl.total.placeholder.ChatPlaceholderSupport
import github.gilbertokpl.total.util.PermissionUtil
import net.milkbowl.vault.chat.Chat
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

object ChatManager {

    private const val MESSAGE_MARKER = "\u0001TOTAL_CHAT_MESSAGE\u0001"
    private const val TARGET_MARKER = "\u0001TOTAL_CHAT_TARGET\u0001"

    private val channels = LinkedHashMap<String, ChatChannel>()
    private val aliases = HashMap<String, String>()
    private val selectedChannels = ConcurrentHashMap<String, String>()
    private val joinedChannels = ConcurrentHashMap<String, MutableSet<String>>()
    private val cooldowns = ConcurrentHashMap<String, Long>()
    private val lastPrivateContacts = ConcurrentHashMap<String, String>()
    private var filteredWords: List<String> = emptyList()
    private var enabled = true
    private var configuredDefault = "local"

    private val vaultChat: Chat?
        get() = try {
            TotalEssentials.getInstance().server.servicesManager
                .getRegistration(Chat::class.java)?.provider
        } catch (_: LinkageError) {
            null
        } catch (_: Exception) {
            null
        }

    fun start() {
        reload()
    }

    fun stop() {
        selectedChannels.clear()
        joinedChannels.clear()
        cooldowns.clear()
        lastPrivateContacts.clear()
    }

    @Synchronized
    fun reload() {
        enabled = ChatConfig.generalActivated
        configuredDefault = normalize(ChatConfig.generalDefaultChannel)
        filteredWords = ChatConfig.generalFilterWords
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        channels.clear()
        aliases.clear()
        registerChannel(ChatChannel(
            "Network", ChatConfig.networkActivated, ChatConfig.networkColor, ChatConfig.networkChatColor,
            ChatConfig.networkMutable, ChatConfig.networkFilter, ChatConfig.networkAutoJoin,
            ChatConfig.networkDefault, ChatConfig.networkDistance.coerceAtLeast(0),
            ChatConfig.networkCooldown.coerceAtLeast(0), ChatConfig.networkBungeecord,
            ChatConfig.networkAliases, optionalPermission(ChatConfig.networkPermission),
            optionalPermission(ChatConfig.networkSpeakPermission), ChatConfig.networkChannelPrefix,
            ChatConfig.networkFormat
        ))
        registerChannel(ChatChannel(
            "Local", ChatConfig.localActivated, ChatConfig.localColor, ChatConfig.localChatColor,
            ChatConfig.localMutable, ChatConfig.localFilter, ChatConfig.localAutoJoin,
            ChatConfig.localDefault, ChatConfig.localDistance.coerceAtLeast(0),
            ChatConfig.localCooldown.coerceAtLeast(0), ChatConfig.localBungeecord,
            ChatConfig.localAliases, optionalPermission(ChatConfig.localPermission),
            optionalPermission(ChatConfig.localSpeakPermission), ChatConfig.localChannelPrefix,
            ChatConfig.localFormat
        ))

        if (configuredDefault !in channels) {
            configuredDefault = channels.entries.firstOrNull { it.value.default }?.key
                ?: channels.keys.firstOrNull().orEmpty()
        }
        selectedChannels.entries.removeIf { it.value !in channels }
        joinedChannels.values.forEach { joined -> joined.removeIf { it !in channels } }
    }

    fun reloadFromDisk(): Boolean {
        if (!TotalEssentials.getCore().reloadConfig()) return false
        reload()
        return true
    }

    private fun registerChannel(channel: ChatChannel) {
        if (!channel.enabled) return
        val key = normalize(channel.name)
        channels[key] = channel
        aliases[key] = key
        channel.aliases.map(::normalize).filter(String::isNotEmpty).forEach { alias -> aliases[alias] = key }
    }

    fun isEnabled(): Boolean = enabled && channels.isNotEmpty()

    fun commandAliases(): List<String> = aliases.keys
        .filter { it != "chat" }
        .distinct()

    fun joinPlayer(player: Player) {
        val playerKey = playerKey(player)
        val joined = joinedChannels.computeIfAbsent(playerKey) { ConcurrentHashMap.newKeySet() }
        channels.forEach { (key, channel) ->
            if (channel.autoJoin && channel.canAccess { PermissionUtil.hasPermission(player, it) }) joined.add(key)
        }
        val preferred = channels[configuredDefault]
            ?.takeIf { it.canAccess { permission -> PermissionUtil.hasPermission(player, permission) } }
            ?.let { configuredDefault }
            ?: joined.firstOrNull()
        if (preferred != null) {
            joined.add(preferred)
            selectedChannels[playerKey] = preferred
        }
    }

    fun leavePlayer(player: Player) {
        val playerKey = playerKey(player)
        selectedChannels.remove(playerKey)
        joinedChannels.remove(playerKey)
        cooldowns.keys.removeIf { it.startsWith("$playerKey:") }
        lastPrivateContacts.remove(playerKey)
        lastPrivateContacts.entries.removeIf { it.value == playerKey }
    }

    fun handleChat(player: Player, rawMessage: String, requestedChannel: String? = null) {
        if (!isEnabled() || !player.isOnline) return
        ensureSession(player)

        val playerKey = playerKey(player)
        val key = requestedChannel?.let(::resolveKey) ?: selectedChannels[playerKey]
        val channel = key?.let(channels::get)
        if (key == null || channel == null) {
            player.sendMessage(message("channel-not-found"))
            return
        }
        if (!channel.canAccess { PermissionUtil.hasPermission(player, it) }) {
            player.sendMessage(message("no-permission"))
            return
        }
        if (!channel.canSpeak { PermissionUtil.hasPermission(player, it) }) {
            player.sendMessage(message("no-speak-permission"))
            return
        }
        if (key !in joinedChannels[playerKey].orEmpty()) {
            player.sendMessage(message("not-listening"))
            return
        }

        val remaining = cooldownRemaining(player, key, channel.cooldown)
        if (remaining > 0) {
            player.sendMessage(message("cooldown").replace("%seconds%", remaining.toString()))
            return
        }

        val filtered = if (channel.filter) filter(rawMessage) else rawMessage
        val formatted = format(channel, player, filtered)
        sendToLocalRecipients(player, channel, key, formatted)
        if (channel.network) VelocityAuthBridge.sendChat(player, channel.name, formatted)
    }

    fun sendPrivateMessage(player: Player, targetInput: String?, rawMessage: String, reply: Boolean) {
        if (!ChatConfig.tellActivated || !player.isOnline) return

        val knownTarget = if (reply) lastPrivateContacts[playerKey(player)] else targetInput
        if (knownTarget != null) {
            val localTarget = org.bukkit.Bukkit.getPlayerExact(knownTarget)
            if (localTarget != null && localTarget.isOnline) {
                deliverLocalPrivateMessage(player, localTarget, rawMessage)
                return
            }
        }

        val filtered = if (ChatConfig.tellFilter) filter(rawMessage) else rawMessage
        val senderFormat = formatPrivate(player, LangConfig.tellSenderFormat, filtered)
        val receiverFormat = formatPrivate(player, LangConfig.tellReceiverFormat, filtered)
        VelocityAuthBridge.sendPrivateMessage(
            player = player,
            target = if (reply && knownTarget == null) "" else knownTarget.orEmpty(),
            reply = reply && knownTarget == null,
            senderFormat = senderFormat,
            receiverFormat = receiverFormat,
            notFoundMessage = color(LangConfig.tellPlayerNotOnline),
            noReplyMessage = color(LangConfig.tellNoReply),
            selfMessage = color(LangConfig.tellCannotSelf)
        )
    }

    private fun deliverLocalPrivateMessage(sender: Player, target: Player, rawMessage: String) {
        if (sender.name.equals(target.name, true)) {
            sender.sendMessage(color(LangConfig.tellCannotSelf))
            return
        }

        val filtered = if (ChatConfig.tellFilter) filter(rawMessage) else rawMessage
        val senderFormat = formatPrivate(sender, LangConfig.tellSenderFormat, filtered)
            .replace("%target%", target.name)
        val receiverFormat = formatPrivate(sender, LangConfig.tellReceiverFormat, filtered)
            .replace("%target%", target.name)
        sender.sendMessage(senderFormat)
        target.sendMessage(receiverFormat)
        lastPrivateContacts[playerKey(sender)] = playerKey(target)
        lastPrivateContacts[playerKey(target)] = playerKey(sender)
    }

    private fun formatPrivate(player: Player, template: String, rawMessage: String): String {
        val playerMessage = PermissionUtil.colorPermission(player, rawMessage)
        val withPlaceholders = ChatPlaceholderSupport.apply(
            player,
            template
                .replace("%player%", player.name)
                .replace("%target%", TARGET_MARKER)
                .replace("%message%", MESSAGE_MARKER)
        )
        return color(
            withPlaceholders
                .replace(TARGET_MARKER, "%target%")
                .replace(MESSAGE_MARKER, playerMessage)
        )
    }

    fun select(player: Player, input: String): Boolean {
        ensureSession(player)
        val key = resolveKey(input) ?: return false
        val channel = channels[key] ?: return false
        if (!channel.canAccess { PermissionUtil.hasPermission(player, it) }) {
            player.sendMessage(message("no-permission"))
            return true
        }
        val playerKey = playerKey(player)
        joinedChannels.computeIfAbsent(playerKey) { ConcurrentHashMap.newKeySet() }.add(key)
        selectedChannels[playerKey] = key
        player.sendMessage(message("channel-selected").replace("%channel%", channel.name))
        return true
    }

    fun join(player: Player, input: String): Boolean {
        val key = resolveKey(input) ?: return false
        val channel = channels[key] ?: return false
        if (!channel.canAccess { PermissionUtil.hasPermission(player, it) }) {
            player.sendMessage(message("no-permission"))
            return true
        }
        joinedChannels.computeIfAbsent(playerKey(player)) { ConcurrentHashMap.newKeySet() }.add(key)
        player.sendMessage(message("channel-joined").replace("%channel%", channel.name))
        return true
    }

    fun leave(player: Player, input: String): Boolean {
        val key = resolveKey(input) ?: return false
        val channel = channels[key] ?: return false
        if (!channel.mutable) {
            player.sendMessage(message("cannot-leave"))
            return true
        }
        val playerKey = playerKey(player)
        joinedChannels[playerKey]?.remove(key)
        if (selectedChannels[playerKey] == key) {
            val next = joinedChannels[playerKey]?.firstOrNull()
            if (next == null) selectedChannels.remove(playerKey)
            else selectedChannels[playerKey] = next
        }
        player.sendMessage(message("channel-left").replace("%channel%", channel.name))
        return true
    }

    fun list(player: Player) {
        ensureSession(player)
        val list = channels.entries
            .filter { it.value.canAccess { permission -> PermissionUtil.hasPermission(player, permission) } }
            .joinToString("&7, ") { (key, channel) ->
                val selected = selectedChannels[playerKey(player)] == key
                "${channel.color}${if (selected) "&l" else ""}${channel.name}"
            }
        player.sendMessage(color(LangConfig.chatListHeader.replace("%channels%", list)))
    }

    fun channelForAlias(alias: String): ChatChannel? = resolveKey(alias)?.let(channels::get)

    fun message(key: String): String = color(when (key) {
        "no-permission" -> LangConfig.chatNoPermission
        "no-speak-permission" -> LangConfig.chatNoSpeakPermission
        "channel-not-found" -> LangConfig.chatChannelNotFound
        "channel-selected" -> LangConfig.chatChannelSelected
        "channel-joined" -> LangConfig.chatChannelJoined
        "channel-left" -> LangConfig.chatChannelLeft
        "cannot-leave" -> LangConfig.chatCannotLeave
        "not-listening" -> LangConfig.chatNotListening
        "cooldown" -> LangConfig.chatCooldown
        "list-header" -> LangConfig.chatListHeader
        "reload" -> LangConfig.chatReload
        else -> key
    })

    private fun ensureSession(player: Player) {
        if (!joinedChannels.containsKey(playerKey(player))) joinPlayer(player)
    }

    private fun resolveKey(value: String): String? = aliases[normalize(value)]

    private fun sendToLocalRecipients(sender: Player, channel: ChatChannel, key: String, message: String) {
        TotalEssentials.getCore().getReflection().getPlayers().forEach { recipient ->
            if (key !in joinedChannels[playerKey(recipient)].orEmpty()) return@forEach
            if (!channel.canAccess { PermissionUtil.hasPermission(recipient, it) }) return@forEach
            if (channel.distance > 0) {
                if (recipient.world != sender.world) return@forEach
                if (recipient.location.distanceSquared(sender.location) > channel.distance.toDouble() * channel.distance) return@forEach
            }
            recipient.sendMessage(message)
        }
    }

    private fun format(channel: ChatChannel, player: Player, rawMessage: String): String {
        val chat = vaultChat
        val prefix = try { chat?.getPlayerPrefix(player) ?: "" } catch (_: Throwable) { "" }
        val suffix = try { chat?.getPlayerSuffix(player) ?: "" } catch (_: Throwable) { "" }
        val playerMessage = if (github.gilbertokpl.total.config.files.MainConfig.addonsColorInChat) {
            val selectedColor = PlayerData.colorCache[player] ?: ""
            selectedColor + PermissionUtil.colorPermission(player, rawMessage)
        } else rawMessage

        val formatWithPlayerPlaceholders = ChatPlaceholderSupport.apply(
            player,
            channel.format
                .replace("{channel}", channel.name)
                .replace("{channel_prefix}", channel.prefix)
                .replace("{venturechat_channel_prefix}", channel.prefix)
                .replace("{vault_prefix}", prefix)
                .replace("{vault_suffix}", suffix)
                .replace("{player}", player.name)
                .replace("{player_name}", player.name)
                .replace("{player_displayname}", player.displayName)
                .replace("{magnata}", MagnataManager.getPrefix(player))
                .replace("{chat_color}", channel.chatColor)
                .replace("{message}", MESSAGE_MARKER)
        )
        return color(formatWithPlayerPlaceholders.replace(MESSAGE_MARKER, playerMessage))
    }

    private fun filter(message: String): String {
        var result = message
        filteredWords.forEach { word ->
            result = Regex(Regex.escape(word), RegexOption.IGNORE_CASE)
                .replace(result) { "*".repeat(it.value.length) }
        }
        return result
    }

    private fun cooldownRemaining(player: Player, key: String, seconds: Int): Long {
        if (seconds <= 0 || PermissionUtil.hasPermission(player, "totalessentials.chat.bypass.cooldown")) return 0
        val now = System.currentTimeMillis()
        val mapKey = "${playerKey(player)}:$key"
        val until = cooldowns[mapKey] ?: 0L
        if (until > now) return ((until - now + 999) / 1000)
        cooldowns[mapKey] = now + seconds * 1000L
        return 0
    }

    private fun normalize(value: String): String = value.trim().lowercase(Locale.ROOT)

    // Player#getUniqueId did not exist in the earliest Bukkit APIs supported by
    // this plugin. Names are stable for the duration of an online chat session.
    private fun playerKey(player: Player): String = normalize(player.name)

    private fun optionalPermission(value: String?): String {
        val permission = value?.trim().orEmpty()
        return if (permission.equals("none", true) || permission.equals("null", true)) "" else permission
    }

    private fun color(value: String): String = ChatColor.translateAlternateColorCodes('&', value)
}
