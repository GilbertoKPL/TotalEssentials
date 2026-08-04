package github.gilbertokpl.total.login

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.LoginData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Backend side of the Velocity authentication bridge.
 *
 * Velocity owns the authentication session for the lifetime of the proxy
 * connection. The lobby reports a successful login and every backend receives
 * the current session state as soon as the player connects to it.
 */
object VelocityAuthBridge : PluginMessageListener {

    const val LEGACY_CHANNEL = "TotalEssentials"
    // Namespaced for modern Bukkit, but short enough for the 16-character
    // Packet250CustomPayload limit used by MCPC and other legacy backends.
    const val MODERN_CHANNEL = "totaless:auth"

    @Volatile
    private var outgoingChannel = LEGACY_CHANNEL

    // Name-based variants avoid Player#getUniqueId linkage on Bukkit 1.5.2.
    // Velocity still accepts/sends the old UUID variants during rolling updates.
    private const val AUTHENTICATED = 4
    private const val SESSION_STATE = 5
    private const val CHAT_MESSAGE = 3
    private const val PRIVATE_MESSAGE = 6
    private const val PRIVATE_REPLY = 7
    private const val SESSION_STATE_REQUEST = 8
    private const val SERVER_CONNECT_REQUEST = 9
    private const val HANDSHAKE_RETRY_TICKS = 5L
    private const val HANDSHAKE_WAIT_TICKS = 10L

    private data class SessionState(
        val authenticated: Boolean,
        val authenticationRequiredHere: Boolean
    )

    private val sessionStates = ConcurrentHashMap<String, SessionState>()

    fun start() {
        val plugin = TotalEssentials.getInstance()
        val messenger = TotalEssentials.getInstance().server.messenger
        var legacyRegistered = false
        var modernRegistered = false

        try {
            messenger.registerOutgoingPluginChannel(plugin, LEGACY_CHANNEL)
            messenger.registerIncomingPluginChannel(plugin, LEGACY_CHANNEL, this)
            legacyRegistered = true
        } catch (_: Throwable) {
        }

        try {
            messenger.registerOutgoingPluginChannel(plugin, MODERN_CHANNEL)
            messenger.registerIncomingPluginChannel(plugin, MODERN_CHANNEL, this)
            modernRegistered = true
        } catch (_: Throwable) {
        }

        outgoingChannel = if (modernRegistered) MODERN_CHANNEL else LEGACY_CHANNEL
        when {
            modernRegistered -> plugin.logger.info("Proxy bridge using modern channel $MODERN_CHANNEL.")
            legacyRegistered -> plugin.logger.info("Proxy bridge using legacy channel $LEGACY_CHANNEL.")
            else -> plugin.logger.severe("Could not register a proxy plugin-message channel.")
        }
    }

    fun stop() {
        val plugin = TotalEssentials.getInstance()
        val messenger = plugin.server.messenger
        listOf(LEGACY_CHANNEL, MODERN_CHANNEL).forEach { channel ->
            try {
                messenger.unregisterOutgoingPluginChannel(plugin, channel)
                messenger.unregisterIncomingPluginChannel(plugin, channel, this)
            } catch (_: Throwable) {
            }
        }
        sessionStates.clear()
    }

    fun clear(player: Player) {
        sessionStates.remove(player.name.lowercase())
    }

    fun beginAuthentication(player: Player, address: String) {
        LoginData.loginAttempts[player] = 0
        LoginData.values[player] = 0
        LoginData.isLoggedIn[player] = false

        requestSessionState(player)
        val task = TotalEssentials.getCore().getTask()
        task.async {
            task.waitTicks(HANDSHAKE_RETRY_TICKS)
            task.sync {
                if (player.isOnline
                    && !LoginData.isPlayerLoggedIn(player)
                    && sessionStates[player.name.lowercase()] == null
                ) {
                    requestSessionState(player)
                }
            }

            task.waitTicks(HANDSHAKE_WAIT_TICKS - HANDSHAKE_RETRY_TICKS)
            task.sync {
                if (!player.isOnline || LoginData.isPlayerLoggedIn(player)) return@sync

                val velocityState = sessionStates[player.name.lowercase()]
                when {
                    velocityState?.authenticated == true -> LoginData.markLoggedIn(player, false)
                    velocityState != null && !velocityState.authenticationRequiredHere -> {
                        // Velocity never intentionally sends an unauthenticated player here.
                        // Keep the player locked if another proxy plugin bypassed the guard.
                    }
                    LoginData.ipAddress[player] == address -> {
                        player.sendMessage(LangConfig.authAutoLogin)
                        LoginData.markLoggedIn(player)
                    }
                    else -> LoginManager.loginMessage(player)
                }
            }
        }
    }

    private fun requestSessionState(player: Player) {
        val payload = ByteArrayOutputStream().use { bytes ->
            DataOutputStream(bytes).use { output ->
                output.writeByte(SESSION_STATE_REQUEST)
                output.writeUTF(player.name)
            }
            bytes.toByteArray()
        }
        sendPayload(player, payload)
    }

    fun notifyAuthenticated(player: Player) {
        if (!player.isOnline) return

        val payload = ByteArrayOutputStream().use { bytes ->
            DataOutputStream(bytes).use { output ->
                output.writeByte(AUTHENTICATED)
                output.writeUTF(player.name)
            }
            bytes.toByteArray()
        }

        sendPayload(player, payload)
        sendLegacyAuthenticated(player)
    }

    private fun sendLegacyAuthenticated(player: Player) {
        // Keeps a new backend compatible with an older Velocity companion,
        // without statically linking Player#getUniqueId on early Bukkit APIs.
        val playerId = try {
            player.javaClass.getMethod("getUniqueId").invoke(player) as? UUID
        } catch (_: Throwable) {
            null
        } ?: return

        val payload = ByteArrayOutputStream().use { bytes ->
            DataOutputStream(bytes).use { output ->
                output.writeByte(1)
                output.writeLong(playerId.mostSignificantBits)
                output.writeLong(playerId.leastSignificantBits)
            }
            bytes.toByteArray()
        }
        sendPayload(player, payload)
    }

    fun sendChat(player: Player, channel: String, formattedMessage: String) {
        if (!player.isOnline) return
        val payload = ByteArrayOutputStream().use { bytes ->
            DataOutputStream(bytes).use { output ->
                output.writeByte(CHAT_MESSAGE)
                output.writeUTF(player.name)
                output.writeUTF(channel)
                output.writeUTF(formattedMessage)
            }
            bytes.toByteArray()
        }
        sendPayload(player, payload)
    }

    fun sendPrivateMessage(
        player: Player,
        target: String,
        reply: Boolean,
        senderFormat: String,
        receiverFormat: String,
        notFoundMessage: String,
        noReplyMessage: String,
        selfMessage: String
    ) {
        if (!player.isOnline) return
        val payload = ByteArrayOutputStream().use { bytes ->
            DataOutputStream(bytes).use { output ->
                output.writeByte(if (reply) PRIVATE_REPLY else PRIVATE_MESSAGE)
                output.writeUTF(player.name)
                output.writeUTF(target)
                output.writeUTF(senderFormat)
                output.writeUTF(receiverFormat)
                output.writeUTF(notFoundMessage)
                output.writeUTF(noReplyMessage)
                output.writeUTF(selfMessage)
            }
            bytes.toByteArray()
        }
        sendPayload(player, payload)
    }

    fun connectServer(player: Player, serverName: String) {
        val payload = ByteArrayOutputStream().use { bytes ->
            DataOutputStream(bytes).use { output ->
                output.writeByte(SERVER_CONNECT_REQUEST)
                output.writeUTF(player.name)
                output.writeUTF(serverName)
                output.writeBoolean(LoginData.isPlayerLoggedIn(player))
                output.writeUTF(LangConfig.totalconnectServerNotFound)
                output.writeUTF(LangConfig.totalconnectAlreadyConnected)
                output.writeUTF(LangConfig.totalconnectConnectionAttempt)
                output.writeUTF(LangConfig.totalconnectSuccess)
                output.writeUTF(LangConfig.totalconnectFailure)
            }
            bytes.toByteArray()
        }
        sendPayload(player, payload)
    }

    private fun sendPayload(player: Player, payload: ByteArray) {
        player.sendPluginMessage(TotalEssentials.getInstance(), outgoingChannel, payload)
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != LEGACY_CHANNEL && channel != MODERN_CHANNEL) return

        try {
            DataInputStream(ByteArrayInputStream(message)).use { input ->
                if (input.readUnsignedByte() != SESSION_STATE) return

                val playerName = input.readUTF()
                if (!playerName.equals(player.name, true)) return

                val state = SessionState(
                    authenticated = input.readBoolean(),
                    authenticationRequiredHere = input.readBoolean()
                )
                sessionStates[playerName.lowercase()] = state

                if (state.authenticated && !LoginData.isPlayerLoggedIn(player)) {
                    // Plugin messages are normally delivered on the server thread. Scheduling
                    // also keeps this safe on hybrid server implementations.
                    TotalEssentials.getCore().getTask().sync {
                        Bukkit.getPlayerExact(playerName)?.let { onlinePlayer ->
                            if (onlinePlayer.isOnline) LoginData.markLoggedIn(onlinePlayer, false)
                        }
                    }
                }
            }
        } catch (_: Exception) {
            TotalEssentials.getInstance().logger.warning("Received an invalid Velocity authentication message.")
        }
    }
}
