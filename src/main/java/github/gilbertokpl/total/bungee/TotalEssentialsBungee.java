package github.gilbertokpl.total.bungee;

import github.gilbertokpl.total.update.SafeUpdateManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;

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
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class TotalEssentialsBungee extends Plugin implements Listener {

    private static final String LEGACY_CHANNEL = "TotalEssentials";
    private static final String MODERN_CHANNEL = "totaless:auth";
    private static final String PREVIOUS_MODERN_CHANNEL = "totalessentials:auth";
    private static final int AUTHENTICATED_UUID = 1;
    private static final int SESSION_STATE_UUID = 2;
    private static final int CHAT_MESSAGE = 3;
    private static final int AUTHENTICATED_NAME = 4;
    private static final int SESSION_STATE_NAME = 5;
    private static final int PRIVATE_MESSAGE = 6;
    private static final int PRIVATE_REPLY = 7;
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

    @Override
    public void onEnable() {
        loadConfiguration();
        ProxyServer.getInstance().registerChannel(LEGACY_CHANNEL);
        try {
            ProxyServer.getInstance().registerChannel(MODERN_CHANNEL);
            // Receive messages from backends that have not yet been updated.
            // The previous 20-character channel must never be sent to MCPC.
            ProxyServer.getInstance().registerChannel(PREVIOUS_MODERN_CHANNEL);
        } catch (IllegalArgumentException ignored) {
            getLogger().warning("This BungeeCord build only accepts the legacy plugin channel.");
        }
        ProxyServer.getInstance().getPluginManager().registerListener(this, this);
        startUpdateManager();
        getLogger().info("Chat, authentication and auto-reconnect bridge enabled.");
        if (reconnectEnabled && ProxyServer.getInstance().getServerInfo(reconnectFallbackServer) == null) {
            getLogger().severe("Auto-reconnect fallback server '" + reconnectFallbackServer
                    + "' is not registered in BungeeCord config.yml.");
        }
    }

    @Override
    public void onDisable() {
        if (updateManager != null) updateManager.close();
        reconnectTasks.values().forEach(ScheduledTask::cancel);
        reconnectTasks.clear();
        reconnectRetentionTasks.values().forEach(ScheduledTask::cancel);
        reconnectRetentionTasks.clear();
        reconnectTargets.clear();
        reconnectAttempts.clear();
        ProxyServer.getInstance().unregisterChannel(LEGACY_CHANNEL);
        try {
            ProxyServer.getInstance().unregisterChannel(MODERN_CHANNEL);
            ProxyServer.getInstance().unregisterChannel(PREVIOUS_MODERN_CHANNEL);
        } catch (IllegalArgumentException ignored) {
        }
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!LEGACY_CHANNEL.equals(event.getTag())
                && !MODERN_CHANNEL.equals(event.getTag())
                && !PREVIOUS_MODERN_CHANNEL.equals(event.getTag())) return;

        // Only a backend may publish chat. A client cannot forge this channel.
        if (!(event.getSender() instanceof Server) || !(event.getReceiver() instanceof ProxiedPlayer)) return;
        event.setCancelled(true);

        Server source = (Server) event.getSender();
        ProxiedPlayer carrier = (ProxiedPlayer) event.getReceiver();
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(event.getData()))) {
            int messageType = input.readUnsignedByte();
            String sourceServer = source.getInfo().getName().toLowerCase();

            if (messageType == AUTHENTICATED_UUID || messageType == AUTHENTICATED_NAME) {
                if (!authenticationServers.contains(sourceServer)) return;
                if (messageType == AUTHENTICATED_UUID) {
                    UUID playerId = new UUID(input.readLong(), input.readLong());
                    if (!carrier.getUniqueId().equals(playerId)) return;
                } else if (!carrier.getName().equalsIgnoreCase(input.readUTF())) {
                    return;
                }
                authenticatedPlayers.add(carrier.getUniqueId());
                return;
            }
            if (messageType == PRIVATE_MESSAGE || messageType == PRIVATE_REPLY) {
                handlePrivateMessage(carrier, input, messageType == PRIVATE_REPLY);
                return;
            }
            if (messageType != CHAT_MESSAGE) return;

            String playerName = input.readUTF();
            if (!carrier.getName().equalsIgnoreCase(playerName)) return;

            String channel = input.readUTF();
            String formattedMessage = input.readUTF();
            if (channel.length() > 64 || formattedMessage.length() > 4096) return;

            BaseComponent[] components = TextComponent.fromLegacyText(formattedMessage);
            for (ProxiedPlayer recipient : ProxyServer.getInstance().getPlayers()) {
                if (recipient.getServer() != null
                        && recipient.getServer().getInfo().equals(source.getInfo())) {
                    continue;
                }
                recipient.sendMessage(components);
            }
        } catch (IOException exception) {
            getLogger().warning("Received an invalid chat message from " + source.getInfo().getName() + ".");
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        if (authenticatedPlayers.contains(event.getPlayer().getUniqueId())) return;

        String target = event.getTarget().getName().toLowerCase();
        if (authenticationServers.contains(target)) return;

        ServerInfo authenticationServer = firstAuthenticationServer();
        if (authenticationServer != null && event.getPlayer().getServer() == null) {
            event.setTarget(authenticationServer);
            return;
        }

        event.setCancelled(true);
        event.getPlayer().sendMessage(legacyMessage(deniedMessage));
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String connectedServer = event.getServer().getInfo().getName().toLowerCase();
        String reconnectTarget = reconnectTargets.get(player.getUniqueId());
        if (reconnectTarget != null && reconnectTarget.equals(connectedServer)) {
            stopReconnect(player.getUniqueId());
            player.sendMessage(legacyMessage(reconnectSuccessMessage));
        } else if (reconnectTarget != null
                && !connectedServer.equals(reconnectFallbackServer)
                && !authenticationServers.contains(connectedServer)) {
            stopReconnect(player.getUniqueId());
        } else if (reconnectTarget != null) {
            resumeReconnect(player, reconnectTarget);
        }

        boolean authenticated = authenticatedPlayers.contains(player.getUniqueId());
        boolean authenticationRequiredHere = authenticationServers.contains(connectedServer);
        event.getServer().sendData(LEGACY_CHANNEL,
                createSessionPayload(player, authenticated, authenticationRequiredHere, false));
        event.getServer().sendData(LEGACY_CHANNEL,
                createSessionPayload(player, authenticated, authenticationRequiredHere, true));
        try {
            event.getServer().sendData(MODERN_CHANNEL,
                    createSessionPayload(player, authenticated, authenticationRequiredHere, true));
        } catch (IllegalArgumentException ignored) {
        }
    }

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        if (!reconnectEnabled) return;

        String failedServer = event.getKickedFrom().getName().toLowerCase();
        if (failedServer.equals(reconnectFallbackServer) || authenticationServers.contains(failedServer)) return;

        ServerInfo fallback = ProxyServer.getInstance().getServerInfo(reconnectFallbackServer);
        if (fallback == null || fallback.equals(event.getKickedFrom())) return;

        event.setCancelled(true);
        event.setCancelServer(fallback);
        event.setKickReasonComponent(legacyMessage(reconnectWaitingMessage));
        event.getPlayer().sendMessage(legacyMessage(reconnectWaitingMessage));
        startReconnect(event.getPlayer(), event.getKickedFrom());
    }

    private byte[] createSessionPayload(
            ProxiedPlayer player,
            boolean authenticated,
            boolean authenticationRequiredHere,
            boolean nameBased
    ) {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             DataOutputStream output = new DataOutputStream(bytes)) {
            output.writeByte(nameBased ? SESSION_STATE_NAME : SESSION_STATE_UUID);
            if (nameBased) {
                output.writeUTF(player.getName());
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

    private void startReconnect(ProxiedPlayer player, ServerInfo target) {
        UUID playerId = player.getUniqueId();
        stopReconnect(playerId);
        reconnectTargets.put(playerId, target.getName().toLowerCase());
        scheduleReconnect(playerId, target);
    }

    private void scheduleReconnect(UUID playerId, ServerInfo target) {
        ScheduledTask previousTask = reconnectTasks.remove(playerId);
        if (previousTask != null) previousTask.cancel();
        ScheduledTask task = ProxyServer.getInstance().getScheduler().schedule(
                this,
                () -> attemptReconnect(playerId, target),
                reconnectDelaySeconds,
                reconnectIntervalSeconds,
                TimeUnit.SECONDS
        );
        reconnectTasks.put(playerId, task);
    }

    private void resumeReconnect(ProxiedPlayer player, String targetName) {
        ScheduledTask retentionTask = reconnectRetentionTasks.remove(player.getUniqueId());
        if (retentionTask != null) retentionTask.cancel();
        if (reconnectTasks.containsKey(player.getUniqueId())) return;

        ServerInfo target = ProxyServer.getInstance().getServerInfo(targetName);
        if (target == null) {
            stopReconnect(player.getUniqueId());
            return;
        }
        scheduleReconnect(player.getUniqueId(), target);
    }

    private void attemptReconnect(UUID playerId, ServerInfo target) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerId);
        if (player == null) {
            retainReconnectAfterDisconnect(playerId);
            return;
        }

        if (player.getServer() != null) {
            String current = player.getServer().getInfo().getName().toLowerCase();
            if (current.equals(target.getName().toLowerCase())) {
                stopReconnect(playerId);
                return;
            }
            if (!current.equals(reconnectFallbackServer) && !authenticationServers.contains(current)) {
                stopReconnect(playerId);
                return;
            }
        }

        if (!reconnectAttempts.add(playerId)) return;
        player.connect(target, (success, error) -> reconnectAttempts.remove(playerId),
                ServerConnectEvent.Reason.PLUGIN);
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
        ScheduledTask[] retentionHolder = new ScheduledTask[1];
        ScheduledTask retentionTask = ProxyServer.getInstance().getScheduler().schedule(
                this,
                () -> {
                    if (!reconnectRetentionTasks.remove(playerId, retentionHolder[0])) return;
                    ProxiedPlayer onlinePlayer = ProxyServer.getInstance().getPlayer(playerId);
                    if (onlinePlayer != null) {
                        String targetName = reconnectTargets.get(playerId);
                        if (targetName != null) resumeReconnect(onlinePlayer, targetName);
                    } else {
                        stopReconnect(playerId);
                    }
                },
                reconnectOfflineRetentionSeconds,
                TimeUnit.SECONDS
        );
        retentionHolder[0] = retentionTask;
        reconnectRetentionTasks.put(playerId, retentionTask);
    }

    private void handlePrivateMessage(ProxiedPlayer sender, DataInputStream input, boolean reply) throws IOException {
        String playerName = input.readUTF();
        if (!sender.getName().equalsIgnoreCase(playerName)) return;

        String requestedTarget = input.readUTF();
        String senderFormat = input.readUTF();
        String receiverFormat = input.readUTF();
        String notFoundMessage = input.readUTF();
        String noReplyMessage = input.readUTF();
        String selfMessage = input.readUTF();

        ProxiedPlayer recipient;
        if (reply) {
            UUID targetId = privateReplies.get(sender.getUniqueId());
            recipient = targetId == null ? null : ProxyServer.getInstance().getPlayer(targetId);
            if (recipient == null) {
                sender.sendMessage(TextComponent.fromLegacyText(noReplyMessage));
                return;
            }
        } else {
            recipient = ProxyServer.getInstance().getPlayer(requestedTarget);
            if (recipient == null) {
                sender.sendMessage(TextComponent.fromLegacyText(notFoundMessage));
                return;
            }
        }

        if (recipient.getUniqueId().equals(sender.getUniqueId())) {
            sender.sendMessage(TextComponent.fromLegacyText(selfMessage));
            return;
        }

        String senderText = senderFormat
                .replace("%player%", sender.getName())
                .replace("%target%", recipient.getName());
        String receiverText = receiverFormat
                .replace("%player%", sender.getName())
                .replace("%target%", recipient.getName());
        sender.sendMessage(TextComponent.fromLegacyText(senderText));
        recipient.sendMessage(TextComponent.fromLegacyText(receiverText));
        privateReplies.put(sender.getUniqueId(), recipient.getUniqueId());
        privateReplies.put(recipient.getUniqueId(), sender.getUniqueId());
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
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

    private ServerInfo firstAuthenticationServer() {
        for (String serverName : authenticationServers) {
            ServerInfo server = ProxyServer.getInstance().getServerInfo(serverName);
            if (server != null) return server;
        }
        return null;
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
        properties.setProperty("auto-update-enabled", "true");
        properties.setProperty("auto-update-interval-minutes", "30");

        Path configPath = getDataFolder().toPath().resolve("bungee-auth.properties");
        try {
            Files.createDirectories(getDataFolder().toPath());
            if (Files.exists(configPath)) {
                try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
                    properties.load(reader);
                }
            }
            try (Writer writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
                properties.store(writer, "TotalEssentials Bungee settings");
            }
        } catch (IOException exception) {
            getLogger().warning("Could not load " + configPath + ". Using defaults.");
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
        autoUpdateEnabled = Boolean.parseBoolean(properties.getProperty("auto-update-enabled", "true"));
        autoUpdateIntervalMinutes = positiveInteger(
                properties.getProperty("auto-update-interval-minutes"),
                30
        );
    }

    private void startUpdateManager() {
        if (!autoUpdateEnabled) return;
        updateManager = new SafeUpdateManager(
                SafeUpdateManager.Platform.BUNGEE,
                getDescription().getVersion(),
                TotalEssentialsBungee.class,
                getDataFolder().toPath(),
                autoUpdateIntervalMinutes,
                new SafeUpdateManager.UpdateLogger() {
                    @Override
                    public void info(String message) {
                        getLogger().info(message);
                    }

                    @Override
                    public void warning(String message, Throwable error) {
                        getLogger().warning(message + " " + error.getMessage());
                    }
                }
        );
        updateManager.start();
    }

    private int positiveInteger(String value, int fallback) {
        try {
            return Math.max(1, Integer.parseInt(value == null ? "" : value.trim()));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private BaseComponent[] legacyMessage(String message) {
        return TextComponent.fromLegacyText(message.replace('&', '\u00a7'));
    }
}
