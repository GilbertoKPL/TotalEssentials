package github.gilbertokpl.total.velocity;

import github.gilbertokpl.total.update.SafeUpdateManager;
import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Plugin(
        id = "totalessentials",
        name = "TotalEssentials",
        version = "1.2.3",
        description = "Velocity authentication bridge for TotalEssentials",
        authors = {"Gilberto"}
)
public final class TotalEssentialsVelocity {

    private static final LegacyChannelIdentifier CHANNEL =
            new LegacyChannelIdentifier("TotalEssentials");
    private static final MinecraftChannelIdentifier MODERN_CHANNEL =
            MinecraftChannelIdentifier.from("totaless:auth");
    private static final MinecraftChannelIdentifier PREVIOUS_MODERN_CHANNEL =
            MinecraftChannelIdentifier.from("totalessentials:auth");
    private static final int AUTHENTICATED = 1;
    private static final int SESSION_STATE = 2;
    private static final int CHAT_MESSAGE = 3;
    private static final int AUTHENTICATED_NAME = 4;
    private static final int SESSION_STATE_NAME = 5;
    private static final int PRIVATE_MESSAGE = 6;
    private static final int PRIVATE_REPLY = 7;
    private static final int SESSION_STATE_REQUEST = 8;
    private static final int SERVER_CONNECT_REQUEST = 9;
    private static final int RECONNECT_STATE_CHECKPOINT_SECONDS = 5;
    private static final String RECONNECT_STATE_FILE = "reconnect-state.properties";

    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataDirectory;
    private final Set<UUID> authenticatedPlayers = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<UUID, UUID> privateReplies = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, String> reconnectTargets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ScheduledTask> reconnectTasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Long> reconnectRemainingMillis = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Long> reconnectActiveSinceNanos = new ConcurrentHashMap<>();
    private final Set<UUID> reconnectAttempts = ConcurrentHashMap.newKeySet();
    private final Set<UUID> reconnectAfterServerFailure = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<UUID, String> lastPlayableServers = new ConcurrentHashMap<>();
    private final Object reconnectStateLock = new Object();
    private Set<String> authenticationServers = Collections.singleton("lobby");
    private String deniedMessage = "Voce precisa se autenticar no lobby antes de trocar de servidor.";
    private boolean reconnectEnabled = true;
    private String reconnectFallbackServer = "lobby";
    private int reconnectDelaySeconds = 3;
    private int reconnectIntervalSeconds = 5;
    private int reconnectTimeoutSeconds = 300;
    private String reconnectWaitingMessage = "&eO servidor caiu. Voce foi enviado ao lobby e tentaremos reconectar automaticamente.";
    private String reconnectSuccessMessage = "&aO servidor voltou. Reconectado com sucesso.";
    private SafeUpdateManager updateManager;
    private ScheduledTask reconnectStateCheckpointTask;
    private boolean reconnectStateSaveWarningLogged;
    private boolean autoUpdateEnabled = true;
    private int autoUpdateIntervalMinutes = 30;
    private boolean motdEnabled = true;
    private boolean maintenanceMode = false;
    private int motdMaximumPlayers = -1;
    private java.util.List<String> motds = Collections.singletonList(
            "&3Minecraft Server \\n&3Esta aberto com %players_online% jogadores!"
    );
    private java.util.List<String> maintenanceMotds = Collections.singletonList(
            "&3Minecraft Server \\n&cServidor em manutencao"
    );

    @Inject
    public TotalEssentialsVelocity(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        loadConfiguration();
        loadReconnectState();
        startReconnectStateCheckpoint();
        proxy.getChannelRegistrar().register(CHANNEL);
        proxy.getChannelRegistrar().register(MODERN_CHANNEL);
        // Receive messages from backends that have not yet been updated. Never
        // send this 20-character identifier to a legacy backend.
        proxy.getChannelRegistrar().register(PREVIOUS_MODERN_CHANNEL);
        logger.info("Authentication bridge enabled. Authentication servers: {}", authenticationServers);
        startUpdateManager();

        for (String serverName : authenticationServers) {
            if (!proxy.getServer(serverName).isPresent()) {
                logger.error(
                        "Authentication server '{}' is not registered in velocity.toml. " +
                                "Players cannot finish the initial connection until this name is corrected.",
                        serverName
                );
            }
        }
        if (reconnectEnabled && !proxy.getServer(reconnectFallbackServer).isPresent()) {
            logger.error("Auto-reconnect fallback server '{}' is not registered in velocity.toml.",
                    reconnectFallbackServer);
        }
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        if (updateManager != null) updateManager.close();
        if (reconnectStateCheckpointTask != null) reconnectStateCheckpointTask.cancel();
        checkpointAllActiveReconnects(false);
        saveReconnectState();
        reconnectTasks.values().forEach(ScheduledTask::cancel);
        reconnectTasks.clear();
        reconnectTargets.clear();
        reconnectRemainingMillis.clear();
        reconnectActiveSinceNanos.clear();
        reconnectAttempts.clear();
        reconnectAfterServerFailure.clear();
        lastPlayableServers.clear();
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!isAuthenticationChannel(event.getIdentifier())) return;

        // Never allow a client to forge a TotalEssentials backend message.
        event.setResult(PluginMessageEvent.ForwardResult.handled());
        if (!(event.getSource() instanceof ServerConnection)) return;

        ServerConnection source = (ServerConnection) event.getSource();
        String sourceServer = source.getServerInfo().getName().toLowerCase();

        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(event.getData()))) {
            int messageType = input.readUnsignedByte();

            if (messageType == CHAT_MESSAGE) {
                handleChatMessage(source, input);
                return;
            }
            if (messageType == PRIVATE_MESSAGE || messageType == PRIVATE_REPLY) {
                handlePrivateMessage(source, input, messageType == PRIVATE_REPLY);
                return;
            }
            if (messageType == SESSION_STATE_REQUEST) {
                String playerName = input.readUTF();
                if (!source.getPlayer().getUsername().equalsIgnoreCase(playerName)) return;
                sendSessionState(source, event.getIdentifier());
                return;
            }
            if (messageType == SERVER_CONNECT_REQUEST) {
                handleServerConnectRequest(source, input);
                return;
            }

            if (messageType == AUTHENTICATED) {
                UUID playerId = new UUID(input.readLong(), input.readLong());
                if (!source.getPlayer().getUniqueId().equals(playerId)) return;
            } else if (messageType == AUTHENTICATED_NAME) {
                String playerName = input.readUTF();
                if (!source.getPlayer().getUsername().equalsIgnoreCase(playerName)) return;
            } else {
                return;
            }

            if (authenticatedPlayers.add(source.getPlayer().getUniqueId())) {
                logger.info("Authentication session opened for {} by backend {}.",
                        source.getPlayer().getUsername(), sourceServer);
            }
        } catch (IOException ignored) {
            logger.warn("Received an invalid authentication message from {}.", sourceServer);
        }
    }

    private void sendSessionState(ServerConnection connection, ChannelIdentifier channel) {
        Player player = connection.getPlayer();
        String connectedServer = connection.getServerInfo().getName().toLowerCase();
        byte[] payload = createSessionPayload(
                player,
                authenticatedPlayers.contains(player.getUniqueId()),
                authenticationServers.contains(connectedServer),
                true
        );
        connection.sendPluginMessage(channel, payload);
    }

    private void handleServerConnectRequest(ServerConnection source, DataInputStream input) throws IOException {
        Player player = source.getPlayer();
        String playerName = input.readUTF();
        if (!player.getUsername().equalsIgnoreCase(playerName)) return;

        String requestedName = input.readUTF().trim().toLowerCase();
        boolean authenticatedByBackend = input.readBoolean();
        String serverNotFoundMessage = readConnectMessage(input);
        String alreadyConnectedMessage = readConnectMessage(input);
        String connectionAttemptMessage = readConnectMessage(input);
        String successMessage = readConnectMessage(input);
        String failureMessage = readConnectMessage(input);
        if (authenticatedByBackend && authenticatedPlayers.add(player.getUniqueId())) {
            logger.info("Authentication session synchronized for {} by backend {} before server transfer.",
                    player.getUsername(), source.getServerInfo().getName());
        }
        if (requestedName.isEmpty() || requestedName.length() > 64
                || !requestedName.matches("[a-z0-9_.-]+")) {
            player.sendMessage(localizedMessage(
                    failureMessage.replace("%server%", requestedName).replace("%reason%", "INVALID_SERVER_NAME")
            ));
            return;
        }

        Optional<RegisteredServer> requestedServer = proxy.getServer(requestedName);
        if (!requestedServer.isPresent()) {
            player.sendMessage(localizedMessage(
                    serverNotFoundMessage.replace("%server%", requestedName)
            ));
            return;
        }

        RegisteredServer target = requestedServer.get();
        if (player.getCurrentServer()
                .map(connection -> connection.getServerInfo().equals(target.getServerInfo()))
                .orElse(false)) {
            player.sendMessage(localizedMessage(
                    alreadyConnectedMessage.replace("%server%", requestedName)
            ));
            return;
        }

        player.sendMessage(localizedMessage(
                connectionAttemptMessage.replace("%server%", requestedName)
        ));
        player.createConnectionRequest(target).connect().whenComplete((result, error) -> {
            if (error != null) {
                player.sendMessage(localizedMessage(
                        failureMessage.replace("%server%", requestedName)
                                .replace("%reason%", safeErrorMessage(error))
                ));
                return;
            }

            if (result.isSuccessful()) {
                player.sendMessage(localizedMessage(
                        successMessage.replace("%server%", requestedName)
                ));
                return;
            }

            ConnectionRequestBuilder.Status status = result.getStatus();
            if (status == ConnectionRequestBuilder.Status.ALREADY_CONNECTED) {
                player.sendMessage(localizedMessage(
                        alreadyConnectedMessage.replace("%server%", requestedName)
                ));
                return;
            }

            String reason = result.getReasonComponent()
                    .map(LegacyComponentSerializer.legacySection()::serialize)
                    .orElse(status.name());
            player.sendMessage(localizedMessage(
                    failureMessage.replace("%server%", requestedName).replace("%reason%", reason)
            ));
        });
    }

    private String readConnectMessage(DataInputStream input) throws IOException {
        String message = input.readUTF();
        if (message.length() > 512) throw new IOException("Connect message is too long.");
        return message;
    }

    private Component localizedMessage(String message) {
        return LegacyComponentSerializer.legacySection().deserialize(message.replace('&', '\u00A7'));
    }

    private String safeErrorMessage(Throwable error) {
        String message = error.getMessage();
        if (message == null || message.trim().isEmpty()) return error.getClass().getSimpleName();
        return message.length() > 160 ? message.substring(0, 160) : message;
    }

    private void handleChatMessage(ServerConnection source, DataInputStream input) throws IOException {
        String playerName = input.readUTF();
        if (!source.getPlayer().getUsername().equalsIgnoreCase(playerName)) return;

        String channel = input.readUTF();
        String formattedMessage = input.readUTF();
        if (channel.length() > 64 || formattedMessage.length() > 4096) return;

        Component component = LegacyComponentSerializer.legacySection().deserialize(formattedMessage);
        for (Player recipient : proxy.getAllPlayers()) {
            Optional<ServerConnection> currentServer = recipient.getCurrentServer();
            if (currentServer.isPresent()
                    && currentServer.get().getServerInfo().equals(source.getServerInfo())) {
                continue;
            }
            recipient.sendMessage(component);
        }
    }

    private void handlePrivateMessage(ServerConnection source, DataInputStream input, boolean reply) throws IOException {
        String playerName = input.readUTF();
        if (!source.getPlayer().getUsername().equalsIgnoreCase(playerName)) return;

        String requestedTarget = input.readUTF();
        String senderFormat = input.readUTF();
        String receiverFormat = input.readUTF();
        String notFoundMessage = input.readUTF();
        String noReplyMessage = input.readUTF();
        String selfMessage = input.readUTF();
        Player sender = source.getPlayer();

        Optional<Player> target;
        if (reply) {
            UUID targetId = privateReplies.get(sender.getUniqueId());
            target = targetId == null ? Optional.empty() : proxy.getPlayer(targetId);
            if (!target.isPresent()) {
                sender.sendMessage(LegacyComponentSerializer.legacySection().deserialize(noReplyMessage));
                return;
            }
        } else {
            target = proxy.getPlayer(requestedTarget);
            if (!target.isPresent()) {
                sender.sendMessage(LegacyComponentSerializer.legacySection().deserialize(notFoundMessage));
                return;
            }
        }

        Player recipient = target.get();
        if (recipient.getUniqueId().equals(sender.getUniqueId())) {
            sender.sendMessage(LegacyComponentSerializer.legacySection().deserialize(selfMessage));
            return;
        }

        String senderText = senderFormat
                .replace("%player%", sender.getUsername())
                .replace("%target%", recipient.getUsername());
        String receiverText = receiverFormat
                .replace("%player%", sender.getUsername())
                .replace("%target%", recipient.getUsername());
        sender.sendMessage(LegacyComponentSerializer.legacySection().deserialize(senderText));
        recipient.sendMessage(LegacyComponentSerializer.legacySection().deserialize(receiverText));
        privateReplies.put(sender.getUniqueId(), recipient.getUniqueId());
        privateReplies.put(recipient.getUniqueId(), sender.getUniqueId());
    }

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event) {
        // ServerPreConnect is the first reliable event emitted for a new proxy
        // session. Resume a retained reconnect here instead of depending only on
        // ServerConnectedEvent, which may run after a stale DisconnectEvent from
        // the previous session.
        UUID playerId = event.getPlayer().getUniqueId();
        String retainedTarget = reconnectTargets.get(playerId);
        if (reconnectEnabled && retainedTarget == null && event.getPreviousServer() == null) {
            retainedTarget = lastPlayableServers.get(playerId);
            if (retainedTarget != null) {
                rememberReconnect(playerId, retainedTarget, false);
            }
        }
        if (retainedTarget != null) {
            resumeReconnect(event.getPlayer(), retainedTarget);
        }

        if (authenticatedPlayers.contains(event.getPlayer().getUniqueId())) return;

        String targetName = event.getOriginalServer().getServerInfo().getName().toLowerCase();
        if (authenticationServers.contains(targetName)) return;

        Optional<RegisteredServer> authenticationServer = authenticationServers.stream()
                .map(proxy::getServer)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        if (authenticationServer.isPresent() && event.getPreviousServer() == null) {
            event.setResult(ServerPreConnectEvent.ServerResult.allowed(authenticationServer.get()));
            return;
        }

        if (!authenticationServer.isPresent() && event.getPreviousServer() == null) {
            event.setResult(ServerPreConnectEvent.ServerResult.denied());
            event.getPlayer().disconnect(Component.text(
                    "O servidor de login nao esta configurado no Velocity. Avise a administracao."
            ));
            return;
        }

        event.setResult(ServerPreConnectEvent.ServerResult.denied());
        event.getPlayer().sendMessage(Component.text(deniedMessage));
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        String connectedServer = event.getServer().getServerInfo().getName().toLowerCase();
        String reconnectTarget = reconnectTargets.get(playerId);
        if (reconnectTarget != null && reconnectTarget.equals(connectedServer)) {
            boolean notifyServerRecovery = reconnectAfterServerFailure.contains(playerId);
            stopReconnect(playerId);
            if (notifyServerRecovery) {
                player.sendMessage(legacyMessage(reconnectSuccessMessage));
            }
        } else if (reconnectTarget != null
                && !connectedServer.equals(reconnectFallbackServer)
                && !authenticationServers.contains(connectedServer)) {
            // A manual move to another playable server takes precedence.
            stopReconnect(playerId);
        } else if (reconnectTarget != null) {
            resumeReconnect(player, reconnectTarget);
        }
        if (!connectedServer.equals(reconnectFallbackServer)
                && !authenticationServers.contains(connectedServer)) {
            lastPlayableServers.put(playerId, connectedServer);
            saveReconnectState();
        } else if (reconnectTarget == null) {
            // Reaching the lobby without an active recovery means the player
            // chose to stay there. Forget the previous playable server so a
            // later proxy reconnect does not send the player back to it.
            lastPlayableServers.remove(playerId);
            saveReconnectState();
        }
        boolean authenticated = authenticatedPlayers.contains(player.getUniqueId());
        boolean authenticationRequiredHere = authenticationServers.contains(
                event.getServer().getServerInfo().getName().toLowerCase()
        );

        byte[] legacyPayload = createSessionPayload(player, authenticated, authenticationRequiredHere, false);
        byte[] namePayload = createSessionPayload(player, authenticated, authenticationRequiredHere, true);

        // Old backends listen only to the legacy identifier. Modern Paper can
        // require the namespaced identifier, so the name-based state is sent to both.
        event.getServer().sendPluginMessage(CHANNEL, legacyPayload);
        event.getServer().sendPluginMessage(CHANNEL, namePayload);
        event.getServer().sendPluginMessage(MODERN_CHANNEL, namePayload);
    }

    @Subscribe
    public void onKickedFromServer(KickedFromServerEvent event) {
        if (!reconnectEnabled) return;

        String failedServer = event.getServer().getServerInfo().getName().toLowerCase();
        if (failedServer.equals(reconnectFallbackServer) || authenticationServers.contains(failedServer)) return;

        Optional<RegisteredServer> fallback = proxy.getServer(reconnectFallbackServer);
        if (!fallback.isPresent() || fallback.get().getServerInfo().equals(event.getServer().getServerInfo())) return;

        boolean alreadyWaiting = reconnectTargets.containsKey(event.getPlayer().getUniqueId())
                && event.getPlayer().getCurrentServer()
                .map(connection -> connection.getServerInfo().equals(fallback.get().getServerInfo()))
                .orElse(false);
        if (alreadyWaiting) {
            event.setResult(KickedFromServerEvent.Notify.create(legacyMessage(reconnectWaitingMessage)));
            return;
        }

        event.setResult(KickedFromServerEvent.RedirectPlayer.create(fallback.get(),
                legacyMessage(reconnectWaitingMessage)));
        startReconnect(event.getPlayer(), event.getServer());
    }

    private void startReconnect(Player player, RegisteredServer target) {
        UUID playerId = player.getUniqueId();
        stopReconnect(playerId);
        rememberReconnect(playerId, target.getServerInfo().getName().toLowerCase(), true);
        scheduleReconnect(playerId, target);
    }

    private void scheduleReconnect(UUID playerId, RegisteredServer target) {
        if (reconnectRemainingMillis.getOrDefault(playerId, 0L) <= 0L) {
            expireReconnect(playerId);
            return;
        }
        ScheduledTask previousTask = reconnectTasks.remove(playerId);
        if (previousTask != null) previousTask.cancel();

        reconnectActiveSinceNanos.put(playerId, System.nanoTime());
        ScheduledTask task = proxy.getScheduler().buildTask(this, scheduledTask ->
                attemptReconnect(playerId, target, scheduledTask)
        ).delay(reconnectDelaySeconds, TimeUnit.SECONDS)
                .repeat(reconnectIntervalSeconds, TimeUnit.SECONDS)
                .schedule();
        reconnectTasks.put(playerId, task);
    }

    private void resumeReconnect(Player player, String targetName) {
        if (reconnectTasks.containsKey(player.getUniqueId())) return;

        Optional<RegisteredServer> target = proxy.getServer(targetName);
        if (!target.isPresent()) {
            pauseReconnect(player.getUniqueId());
            return;
        }
        scheduleReconnect(player.getUniqueId(), target.get());
    }

    private void attemptReconnect(UUID playerId, RegisteredServer target, ScheduledTask scheduledTask) {
        Optional<Player> optionalPlayer = proxy.getPlayer(playerId);
        if (!optionalPlayer.isPresent()) {
            scheduledTask.cancel();
            retainReconnectAfterDisconnect(playerId);
            return;
        }
        if (checkpointReconnect(playerId, true) <= 0L) {
            expireReconnect(playerId);
            return;
        }

        Player player = optionalPlayer.get();
        Optional<ServerConnection> current = player.getCurrentServer();
        if (current.isPresent()) {
            String currentName = current.get().getServerInfo().getName().toLowerCase();
            if (currentName.equals(target.getServerInfo().getName().toLowerCase())) {
                stopReconnect(playerId);
                return;
            }
            if (!currentName.equals(reconnectFallbackServer) && !authenticationServers.contains(currentName)) {
                stopReconnect(playerId);
                return;
            }
        }

        if (!reconnectAttempts.add(playerId)) return;
        player.createConnectionRequest(target).connect().whenComplete((result, error) -> {
            reconnectAttempts.remove(playerId);
            // ServerConnectedEvent owns the final cleanup and success message.
        });
    }

    private void stopReconnect(UUID playerId) {
        ScheduledTask task = reconnectTasks.remove(playerId);
        if (task != null) task.cancel();
        reconnectTargets.remove(playerId);
        reconnectRemainingMillis.remove(playerId);
        reconnectActiveSinceNanos.remove(playerId);
        reconnectAttempts.remove(playerId);
        reconnectAfterServerFailure.remove(playerId);
        saveReconnectState();
    }

    private void expireReconnect(UUID playerId) {
        stopReconnect(playerId);
        lastPlayableServers.remove(playerId);
        saveReconnectState();
    }

    private void retainReconnectAfterDisconnect(UUID playerId) {
        ScheduledTask task = reconnectTasks.remove(playerId);
        if (task != null) task.cancel();
        reconnectAttempts.remove(playerId);
        pauseReconnect(playerId);
    }

    private void rememberReconnect(UUID playerId, String targetName, boolean serverFailure) {
        synchronized (reconnectStateLock) {
            reconnectTargets.put(playerId, targetName);
            reconnectRemainingMillis.put(playerId, reconnectTimeoutMillis());
            reconnectActiveSinceNanos.remove(playerId);
            if (serverFailure) {
                reconnectAfterServerFailure.add(playerId);
            } else {
                reconnectAfterServerFailure.remove(playerId);
            }
        }
        saveReconnectState();
    }

    private void pauseReconnect(UUID playerId) {
        checkpointReconnect(playerId, false);
        saveReconnectState();
    }

    private long checkpointReconnect(UUID playerId, boolean keepRunning) {
        synchronized (reconnectStateLock) {
            Long startedAt = reconnectActiveSinceNanos.remove(playerId);
            long remaining = reconnectRemainingMillis.getOrDefault(playerId, 0L);
            if (startedAt != null) {
                long elapsedNanos = Math.max(0L, System.nanoTime() - startedAt);
                long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(elapsedNanos);
                remaining = Math.max(0L, remaining - elapsedMillis);
                reconnectRemainingMillis.put(playerId, remaining);
            }
            if (keepRunning && remaining > 0L && reconnectTargets.containsKey(playerId)) {
                reconnectActiveSinceNanos.put(playerId, System.nanoTime());
            }
            return remaining;
        }
    }

    private void checkpointAllActiveReconnects(boolean keepRunning) {
        for (UUID playerId : reconnectActiveSinceNanos.keySet()) {
            checkpointReconnect(playerId, keepRunning);
        }
    }

    private long reconnectTimeoutMillis() {
        return TimeUnit.SECONDS.toMillis(reconnectTimeoutSeconds);
    }

    private void startReconnectStateCheckpoint() {
        reconnectStateCheckpointTask = proxy.getScheduler().buildTask(this, () -> {
            if (reconnectActiveSinceNanos.isEmpty()) return;
            checkpointAllActiveReconnects(true);
            saveReconnectState();
        }).delay(RECONNECT_STATE_CHECKPOINT_SECONDS, TimeUnit.SECONDS)
                .repeat(RECONNECT_STATE_CHECKPOINT_SECONDS, TimeUnit.SECONDS)
                .schedule();
    }

    private void loadReconnectState() {
        Path statePath = dataDirectory.resolve(RECONNECT_STATE_FILE);
        if (!Files.exists(statePath)) return;

        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(statePath, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException exception) {
            logger.warn("Could not load persistent auto-reconnect state from {}.", statePath, exception);
            return;
        }

        int restoredReconnects = 0;
        for (String key : properties.stringPropertyNames()) {
            if (!key.startsWith("reconnect.") || !key.endsWith(".target")) continue;

            String playerIdText = key.substring("reconnect.".length(), key.length() - ".target".length());
            try {
                UUID playerId = UUID.fromString(playerIdText);
                String targetName = properties.getProperty(key, "").trim().toLowerCase();
                long remainingMillis = Long.parseLong(properties.getProperty(
                        "reconnect." + playerIdText + ".remaining-millis",
                        Long.toString(reconnectTimeoutMillis())
                ));
                if (!isValidServerName(targetName) || remainingMillis <= 0L) continue;

                reconnectTargets.put(playerId, targetName);
                reconnectRemainingMillis.put(playerId, Math.min(remainingMillis, reconnectTimeoutMillis()));
                if (Boolean.parseBoolean(properties.getProperty(
                        "reconnect." + playerIdText + ".server-failure",
                        "false"
                ))) {
                    reconnectAfterServerFailure.add(playerId);
                }
                restoredReconnects++;
            } catch (IllegalArgumentException ignored) {
                logger.warn("Ignoring an invalid auto-reconnect entry named '{}'.", key);
            }
        }

        for (String key : properties.stringPropertyNames()) {
            if (!key.startsWith("last-playable.")) continue;
            try {
                UUID playerId = UUID.fromString(key.substring("last-playable.".length()));
                String serverName = properties.getProperty(key, "").trim().toLowerCase();
                if (isValidServerName(serverName)) lastPlayableServers.put(playerId, serverName);
            } catch (IllegalArgumentException ignored) {
                logger.warn("Ignoring an invalid last-server entry named '{}'.", key);
            }
        }

        if (restoredReconnects > 0 || !lastPlayableServers.isEmpty()) {
            logger.info(
                    "Loaded local reconnect state: {} active recoveries and {} remembered destinations.",
                    restoredReconnects,
                    lastPlayableServers.size()
            );
        }
    }

    private void saveReconnectState() {
        synchronized (reconnectStateLock) {
            Properties properties = new Properties();
            for (java.util.Map.Entry<UUID, String> entry : reconnectTargets.entrySet()) {
                long remainingMillis = reconnectRemainingMillis.getOrDefault(
                        entry.getKey(),
                        reconnectTimeoutMillis()
                );
                if (remainingMillis <= 0L) continue;

                String prefix = "reconnect." + entry.getKey();
                properties.setProperty(prefix + ".target", entry.getValue());
                properties.setProperty(prefix + ".remaining-millis", Long.toString(remainingMillis));
                properties.setProperty(
                        prefix + ".server-failure",
                        Boolean.toString(reconnectAfterServerFailure.contains(entry.getKey()))
                );
            }
            for (java.util.Map.Entry<UUID, String> entry : lastPlayableServers.entrySet()) {
                if (reconnectTargets.containsKey(entry.getKey())
                        && reconnectRemainingMillis.getOrDefault(entry.getKey(), 0L) <= 0L) {
                    continue;
                }
                properties.setProperty("last-playable." + entry.getKey(), entry.getValue());
            }

            Path statePath = dataDirectory.resolve(RECONNECT_STATE_FILE);
            Path temporaryPath = dataDirectory.resolve(RECONNECT_STATE_FILE + ".tmp");
            try {
                Files.createDirectories(dataDirectory);
                try (Writer writer = Files.newBufferedWriter(temporaryPath, StandardCharsets.UTF_8)) {
                    properties.store(writer, "TotalEssentials persistent reconnect state");
                }
                try {
                    Files.move(
                            temporaryPath,
                            statePath,
                            StandardCopyOption.ATOMIC_MOVE,
                            StandardCopyOption.REPLACE_EXISTING
                    );
                } catch (AtomicMoveNotSupportedException ignored) {
                    Files.move(temporaryPath, statePath, StandardCopyOption.REPLACE_EXISTING);
                }
                reconnectStateSaveWarningLogged = false;
            } catch (IOException exception) {
                if (!reconnectStateSaveWarningLogged) {
                    logger.warn("Could not save persistent auto-reconnect state to {}.", statePath, exception);
                    reconnectStateSaveWarningLogged = true;
                }
            }
        }
    }

    private boolean isValidServerName(String serverName) {
        return !serverName.isEmpty()
                && serverName.length() <= 64
                && serverName.matches("[a-z0-9_.-]+");
    }

    private Component legacyMessage(String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    private byte[] createSessionPayload(
            Player player,
            boolean authenticated,
            boolean authenticationRequiredHere,
            boolean nameBased
    ) {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             DataOutputStream output = new DataOutputStream(bytes)) {
            output.writeByte(nameBased ? SESSION_STATE_NAME : SESSION_STATE);
            if (nameBased) {
                output.writeUTF(player.getUsername());
            } else {
                output.writeLong(player.getUniqueId().getMostSignificantBits());
                output.writeLong(player.getUniqueId().getLeastSignificantBits());
            }
            output.writeBoolean(authenticated);
            output.writeBoolean(authenticationRequiredHere);
            return bytes.toByteArray();
        } catch (IOException impossible) {
            return new byte[0];
        }
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        authenticatedPlayers.remove(playerId);
        privateReplies.remove(playerId);
        privateReplies.entrySet().removeIf(entry -> entry.getValue().equals(playerId));

        if (reconnectEnabled && !reconnectTargets.containsKey(playerId)) {
            String lastPlayableServer = lastPlayableServers.get(playerId);
            if (lastPlayableServer != null) {
                rememberReconnect(playerId, lastPlayableServer, false);
            }
        }

        Optional<Player> activePlayer = proxy.getPlayer(playerId);
        if (activePlayer.isPresent() && activePlayer.get() != event.getPlayer()) {
            // A new session with the same UUID has already replaced the one that
            // emitted this event. Do not cancel the reconnect task resumed by it.
            String targetName = reconnectTargets.get(playerId);
            if (targetName != null) resumeReconnect(activePlayer.get(), targetName);
            return;
        }

        if (reconnectTargets.containsKey(playerId)) {
            retainReconnectAfterDisconnect(playerId);
        } else {
            stopReconnect(playerId);
            lastPlayableServers.remove(playerId);
            saveReconnectState();
        }
    }

    @Subscribe(order = PostOrder.LAST)
    public void onProxyPing(ProxyPingEvent event) {
        if (!motdEnabled) return;

        java.util.List<String> selectedList = maintenanceMode ? maintenanceMotds : motds;
        if (selectedList.isEmpty()) return;

        int displayedMaximum = motdMaximumPlayers >= 0
                ? motdMaximumPlayers
                : event.getPing().getPlayers().map(players -> players.getMax()).orElse(0);
        String selected = selectedList.get(ThreadLocalRandom.current().nextInt(selectedList.size()))
                .replace("%players_online%", Integer.toString(proxy.getPlayerCount()))
                .replace("%players_max%", Integer.toString(displayedMaximum))
                .replace("\\n", "\n");

        Component description = LegacyComponentSerializer.legacyAmpersand().deserialize(selected);
        com.velocitypowered.api.proxy.server.ServerPing.Builder builder = event.getPing()
                .asBuilder()
                .description(description)
                .onlinePlayers(proxy.getPlayerCount());

        if (motdMaximumPlayers >= 0) builder.maximumPlayers(motdMaximumPlayers);
        event.setPing(builder.build());
    }

    private boolean isAuthenticationChannel(ChannelIdentifier identifier) {
        return CHANNEL.equals(identifier)
                || MODERN_CHANNEL.equals(identifier)
                || PREVIOUS_MODERN_CHANNEL.equals(identifier);
    }

    private void loadConfiguration() {
        Properties properties = new Properties();
        properties.setProperty("authentication-servers", "lobby");
        properties.setProperty("not-authenticated-message", deniedMessage);
        properties.setProperty("reconnect-enabled", "true");
        properties.setProperty("reconnect-fallback-server", "lobby");
        properties.setProperty("reconnect-delay-seconds", "3");
        properties.setProperty("reconnect-interval-seconds", "5");
        properties.setProperty("reconnect-timeout-seconds", "300");
        properties.setProperty("reconnect-waiting-message", reconnectWaitingMessage);
        properties.setProperty("reconnect-success-message", reconnectSuccessMessage);
        properties.setProperty("motd-enabled", "true");
        properties.setProperty(
                "motd-list",
                "&3Minecraft Server \\n&3Esta aberto com %players_online% jogadores!|" +
                        "&9Minecraft Server \\n&9Temos %players_online% jogadores online!"
        );
        properties.setProperty("maintenance-mode", "false");
        properties.setProperty(
                "maintenance-motd-list",
                "&3Minecraft Server \\n&cServidor em manutencao"
        );
        properties.setProperty("motd-maximum-players", "-1");
        properties.setProperty("auto-update-enabled", "true");
        properties.setProperty("auto-update-interval-minutes", "30");

        Path configPath = dataDirectory.resolve("velocity-auth.properties");
        try {
            Files.createDirectories(dataDirectory);
            if (Files.exists(configPath)) {
                try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
                    properties.load(reader);
                }
            }
            // Also writes newly introduced defaults into configurations created
            // by older versions of the companion.
            try (Writer writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
                properties.store(writer, "TotalEssentials Velocity settings");
            }
        } catch (IOException exception) {
            logger.error("Could not load {}. Using defaults.", configPath, exception);
        }

        Set<String> configuredServers = Arrays.stream(
                        properties.getProperty("authentication-servers", "lobby").split(",")
                )
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toCollection(HashSet::new));

        if (!configuredServers.isEmpty()) authenticationServers = configuredServers;
        deniedMessage = properties.getProperty("not-authenticated-message", deniedMessage);
        reconnectEnabled = Boolean.parseBoolean(properties.getProperty("reconnect-enabled", "true"));
        reconnectFallbackServer = properties.getProperty("reconnect-fallback-server", "lobby")
                .trim().toLowerCase();
        reconnectDelaySeconds = positiveInteger(properties.getProperty("reconnect-delay-seconds"), 3);
        reconnectIntervalSeconds = positiveInteger(properties.getProperty("reconnect-interval-seconds"), 5);
        reconnectTimeoutSeconds = positiveInteger(
                properties.getProperty("reconnect-timeout-seconds"),
                300
        );
        reconnectWaitingMessage = properties.getProperty("reconnect-waiting-message", reconnectWaitingMessage);
        reconnectSuccessMessage = properties.getProperty("reconnect-success-message", reconnectSuccessMessage);
        motdEnabled = Boolean.parseBoolean(properties.getProperty("motd-enabled", "true"));
        maintenanceMode = Boolean.parseBoolean(properties.getProperty("maintenance-mode", "false"));
        motds = parseMotdList(properties.getProperty("motd-list", ""));
        maintenanceMotds = parseMotdList(properties.getProperty("maintenance-motd-list", ""));
        autoUpdateEnabled = Boolean.parseBoolean(properties.getProperty("auto-update-enabled", "true"));
        autoUpdateIntervalMinutes = positiveInteger(
                properties.getProperty("auto-update-interval-minutes"),
                30
        );
        try {
            motdMaximumPlayers = Integer.parseInt(
                    properties.getProperty("motd-maximum-players", "-1").trim()
            );
        } catch (NumberFormatException ignored) {
            motdMaximumPlayers = -1;
        }
    }

    private void startUpdateManager() {
        if (!autoUpdateEnabled) return;
        updateManager = new SafeUpdateManager(
                SafeUpdateManager.Platform.VELOCITY,
                TotalEssentialsVelocity.class.getAnnotation(Plugin.class).version(),
                TotalEssentialsVelocity.class,
                dataDirectory,
                autoUpdateIntervalMinutes,
                new SafeUpdateManager.UpdateLogger() {
                    @Override
                    public void info(String message) {
                        logger.info(message);
                    }

                    @Override
                    public void warning(String message, Throwable error) {
                        logger.warn(message, error);
                    }
                }
        );
        updateManager.start();
    }

    private java.util.List<String> parseMotdList(String value) {
        return Arrays.stream(value.split("\\|"))
                .map(String::trim)
                .filter(motd -> !motd.isEmpty())
                .collect(Collectors.toList());
    }

    private int positiveInteger(String value, int fallback) {
        try {
            return Math.max(1, Integer.parseInt(value == null ? "" : value.trim()));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
