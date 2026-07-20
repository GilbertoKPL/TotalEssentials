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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataDirectory;
    private final Set<UUID> authenticatedPlayers = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<UUID, UUID> privateReplies = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, String> reconnectTargets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ScheduledTask> reconnectTasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ScheduledTask> reconnectRetentionTasks = new ConcurrentHashMap<>();
    private final Set<UUID> reconnectAttempts = ConcurrentHashMap.newKeySet();
    private Set<String> authenticationServers = Collections.singleton("lobby");
    private String deniedMessage = "Voce precisa se autenticar no lobby antes de trocar de servidor.";
    private boolean reconnectEnabled = true;
    private String reconnectFallbackServer = "lobby";
    private int reconnectDelaySeconds = 3;
    private int reconnectIntervalSeconds = 5;
    private int reconnectOfflineRetentionSeconds = 60;
    private String reconnectWaitingMessage = "&eO servidor caiu. Voce foi enviado ao lobby e tentaremos reconectar automaticamente.";
    private String reconnectSuccessMessage = "&aO servidor voltou. Reconectado com sucesso.";
    private SafeUpdateManager updateManager;
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
        reconnectTasks.values().forEach(ScheduledTask::cancel);
        reconnectTasks.clear();
        reconnectRetentionTasks.values().forEach(ScheduledTask::cancel);
        reconnectRetentionTasks.clear();
        reconnectTargets.clear();
        reconnectAttempts.clear();
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

            if (!authenticationServers.contains(sourceServer)) return;
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
        String connectedServer = event.getServer().getServerInfo().getName().toLowerCase();
        String reconnectTarget = reconnectTargets.get(player.getUniqueId());
        if (reconnectTarget != null && reconnectTarget.equals(connectedServer)) {
            stopReconnect(player.getUniqueId());
            player.sendMessage(legacyMessage(reconnectSuccessMessage));
        } else if (reconnectTarget != null
                && !connectedServer.equals(reconnectFallbackServer)
                && !authenticationServers.contains(connectedServer)) {
            // A manual move to another playable server takes precedence.
            stopReconnect(player.getUniqueId());
        } else if (reconnectTarget != null) {
            resumeReconnect(player, reconnectTarget);
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
        reconnectTargets.put(playerId, target.getServerInfo().getName().toLowerCase());

        scheduleReconnect(playerId, target);
    }

    private void scheduleReconnect(UUID playerId, RegisteredServer target) {
        ScheduledTask previousTask = reconnectTasks.remove(playerId);
        if (previousTask != null) previousTask.cancel();

        ScheduledTask task = proxy.getScheduler().buildTask(this, scheduledTask ->
                attemptReconnect(playerId, target, scheduledTask)
        ).delay(reconnectDelaySeconds, TimeUnit.SECONDS)
                .repeat(reconnectIntervalSeconds, TimeUnit.SECONDS)
                .schedule();
        reconnectTasks.put(playerId, task);
    }

    private void resumeReconnect(Player player, String targetName) {
        ScheduledTask retentionTask = reconnectRetentionTasks.remove(player.getUniqueId());
        if (retentionTask != null) retentionTask.cancel();
        if (reconnectTasks.containsKey(player.getUniqueId())) return;

        Optional<RegisteredServer> target = proxy.getServer(targetName);
        if (!target.isPresent()) {
            stopReconnect(player.getUniqueId());
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
        ScheduledTask retentionTask = reconnectRetentionTasks.remove(playerId);
        if (retentionTask != null) retentionTask.cancel();
        reconnectTargets.remove(playerId);
        reconnectAttempts.remove(playerId);
    }

    private void retainReconnectAfterDisconnect(UUID playerId) {
        ScheduledTask task = reconnectTasks.remove(playerId);
        if (task != null) task.cancel();
        reconnectAttempts.remove(playerId);

        ScheduledTask previousRetention = reconnectRetentionTasks.remove(playerId);
        if (previousRetention != null) previousRetention.cancel();
        ScheduledTask retentionTask = proxy.getScheduler().buildTask(this, scheduledTask -> {
            if (!reconnectRetentionTasks.remove(playerId, scheduledTask)) return;
            Optional<Player> onlinePlayer = proxy.getPlayer(playerId);
            if (onlinePlayer.isPresent()) {
                String targetName = reconnectTargets.get(playerId);
                if (targetName != null) resumeReconnect(onlinePlayer.get(), targetName);
            } else {
                stopReconnect(playerId);
            }
        }).delay(reconnectOfflineRetentionSeconds, TimeUnit.SECONDS).schedule();
        reconnectRetentionTasks.put(playerId, retentionTask);
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
        if (reconnectTargets.containsKey(playerId)) {
            retainReconnectAfterDisconnect(playerId);
        } else {
            stopReconnect(playerId);
        }
        authenticatedPlayers.remove(playerId);
        privateReplies.remove(playerId);
        privateReplies.entrySet().removeIf(entry -> entry.getValue().equals(playerId));
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
        properties.setProperty("reconnect-offline-retention-seconds", "60");
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
        reconnectOfflineRetentionSeconds = positiveInteger(
                properties.getProperty("reconnect-offline-retention-seconds"),
                60
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
