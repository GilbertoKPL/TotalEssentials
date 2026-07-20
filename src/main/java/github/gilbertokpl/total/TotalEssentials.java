package github.gilbertokpl.total;

import github.gilbertokpl.core.TotalCore;
import github.gilbertokpl.core.utils.ConsoleColorUtil;
import github.gilbertokpl.total.cache.internal.InternalLoader;
import github.gilbertokpl.total.cache.loop.ClearEntitiesLoop;
import github.gilbertokpl.total.cache.loop.PluginLoop;
import github.gilbertokpl.total.cache.sql.*;
import github.gilbertokpl.total.chat.ChatManager;
import github.gilbertokpl.total.config.files.LangConfig;
import github.gilbertokpl.total.config.files.MainConfig;
import github.gilbertokpl.total.discord.DiscordManager;
import github.gilbertokpl.total.economy.EconomyHolder;
import github.gilbertokpl.total.filter.Filter;
import github.gilbertokpl.total.login.VelocityAuthBridge;
import github.gilbertokpl.total.placeholder.TotalPlaceholderExpansion;
import github.gilbertokpl.total.update.SafeUpdateManager;
import github.gilbertokpl.total.util.*;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Paths;
import java.util.Objects;

public class TotalEssentials extends JavaPlugin {

    // ====== Static references ======
    private static TotalEssentials instance;
    private static TotalCore totalCore;
    private static Object permission;


    // ====== State flags ======
    public static boolean lowVersion = false;
    private SafeUpdateManager updateManager;

    // ====== Getters ======
    public static TotalEssentials getInstance() {
        return instance;
    }
    public static TotalCore getCore() {
        return totalCore;
    }
    public static boolean isLowVersion() {
        return lowVersion;
    }
    public static Object getPermission() {
        return permission;
    }

    @Override
    public void onLoad() {
        // Load libraries from Maven repos and inject into classloader
        LibraryLoader loader = new LibraryLoader(
                getLogger(),
                Paths.get(getDataFolder().getPath().replace(".paper-remapped" + File.separator, "")),
                getClass().getClassLoader()
        );
        loader.cleanOldLibs();
        if (!loader.loadAll()) {
            getLogger().severe("Falha ao carregar dependencias! O plugin pode nao funcionar corretamente.");
        }

        instance = this;
        totalCore = new TotalCore(this);

        MaterialUtil.INSTANCE.startMaterials();
        totalCore.startConfig("github.gilbertokpl.total.config.files");

        if (MainConfig.moneyActivated) {
            Bukkit.getServicesManager().register(
                    net.milkbowl.vault.economy.Economy.class,
                    new EconomyHolder(),
                    instance,
                    ServicePriority.Highest
            );
        }
    }

    @Override
    public void onEnable() {
        printConsoleBanner();

        Runtime runtime = Runtime.getRuntime();

        EnchantUtil.INSTANCE.startEnchantments();

        long before = runtime.totalMemory() - runtime.freeMemory();

        ChatManager.INSTANCE.start();
        startCorePlugin();
        VelocityAuthBridge.INSTANCE.start();

        long after = runtime.totalMemory() - runtime.freeMemory();

        initPermissions();
        initPlaceholders();
        initLoaders();
        initVersionCheck();
        initDiscord();
        initLoops();
        initUpdateManager();
        instance.getServer().getLogger().setFilter(new Filter());

        getLogger().info("The cache is using approximately: " + ((after - before) / 1024 / 1024) + " MB");
    }

    @Override
    public void onDisable() {
        if (updateManager != null) updateManager.close();

        VelocityAuthBridge.INSTANCE.stop();
        ChatManager.INSTANCE.stop();

        CacheIntegrityChecker.INSTANCE.syncAllCaches();

        PlayerUtil.INSTANCE.savePlaytime();
        ServerUtil.INSTANCE.consoleMessage(ConsoleColorUtil.YELLOW.getColor() + LangConfig.generalSaveDataMessage + ConsoleColorUtil.RESET.getColor());
        totalCore.stop();
        ServerUtil.INSTANCE.consoleMessage(ConsoleColorUtil.YELLOW.getColor() + LangConfig.generalSaveDataSuccess + ConsoleColorUtil.RESET.getColor());

        TotalEssentials.getCore().getTask().disable();
        if (MainConfig.discordbotConnectDiscordChat) {
            DiscordManager.INSTANCE.sendDiscordMessage(
                    LangConfig.discordchatServerClose,
                    true,
                    true,
                    null
            );
        }
    }
    private void startCorePlugin() {
        totalCore.start(
                "github.gilbertokpl.total.commands",
                "github.gilbertokpl.total.listeners",
                "github.gilbertokpl.total.cache.data",
                java.util.Arrays.asList(
                        KitsDataSQL.INSTANCE,
                        PlayerDataSQL.INSTANCE,
                        SpawnDataSQL.INSTANCE,
                        WarpsDataSQL.INSTANCE,
                        LoginDataSQL.INSTANCE,
                        VipDataSQL.INSTANCE,
                        VipKeysSQL.INSTANCE,
                        ShopDataSQL.INSTANCE
                )
        );
    }

    private void initLoaders() {
        InternalLoader.INSTANCE.start(
                MainConfig.announcementsListAnnounce,
                LangConfig.deathmessagesCauseReplacer,
                LangConfig.deathmessagesEntityReplacer
        );
        ServerUtil.INSTANCE.initializeFeatures();
    }

    private void initPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) return;

        if (new TotalPlaceholderExpansion(this).register()) {
            getLogger().info("PlaceholderAPI integration enabled.");
        }
    }

    private void initVersionCheck() {
        if (Bukkit.getBukkitVersion().contains("1.5.2") || Bukkit.getVersion().contains("1.5.2")) {
            lowVersion = true;
        }
    }

    private void initDiscord() {
        if (MainConfig.discordbotToken.isEmpty()) return;
        JDALogger.setFallbackLoggerEnabled(false);
        DiscordManager.INSTANCE.startBot();
        if (MainConfig.discordbotConnectDiscordChat) {
            DiscordManager.INSTANCE.sendDiscordMessage(
                    LangConfig.discordchatServerStart,
                    true,
                    true,
                    null
            );
        }
    }

    private void initLoops() {
        ClearEntitiesLoop.INSTANCE.start();
        PluginLoop.INSTANCE.start();
    }

    private void initPermissions() {
        try {
            permission = Objects.requireNonNull(
                    Bukkit.getServer().getServicesManager()
                            .getRegistration(net.milkbowl.vault.permission.Permission.class)
            ).getProvider();
        } catch (NoClassDefFoundError | NullPointerException ignored) {}

        if (permission == null) {
            try {
                permission = Objects.requireNonNull(
                        Bukkit.getServer().getServicesManager()
                                .getRegistration(net.milkbowl.vault2.permission.Permission.class)
                ).getProvider();
            } catch (NoClassDefFoundError | NullPointerException ee) {
                getLogger().severe("VAULT is missing, the VIP addon will not work properly!");
            }
        }
    }

    private void initUpdateManager() {
        if (!Boolean.TRUE.equals(MainConfig.generalAutoUpdate)) return;

        updateManager = new SafeUpdateManager(
                SafeUpdateManager.Platform.BUKKIT,
                getDescription().getVersion(),
                TotalEssentials.class,
                getDataFolder().toPath(),
                MainConfig.generalAutoUpdateIntervalMinutes == null
                        ? 30
                        : MainConfig.generalAutoUpdateIntervalMinutes,
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

    private static final String RESET = "\u001B[0m";
    private static final String ORANGE = "\u001B[38;5;208m"; // cor laranja vibrante
    private static final String PURPLE = "\u001B[35m";       // roxo do Kotlin
    private static final String BLUE = "\u001B[34m";         // azul do Kotlin

    private void printConsoleBanner() {
        Bukkit.getLogger().info("");
        Bukkit.getLogger().info(ORANGE + "   █████  █████  █████   ██   █   "+BLUE+"  ████   ███   ███   ████  █   █  █████  █   ██   █     ███" + RESET);
        Bukkit.getLogger().info(ORANGE + "     █    █   █    █    █  █  █   "+BLUE+"  █     █     █      █     ██  █    █    █  █  █  █    █" + RESET);
        Bukkit.getLogger().info(ORANGE + "     █    █   █    █    ████  █   "+BLUE+"  ████   ███   ███   ████  █ █ █    █    █  ████  █     ███" + RESET);
        Bukkit.getLogger().info(ORANGE + "     █    █   █    █    █  █  █   "+BLUE+"  █         █     █  █     █  ██    █    █  █  █  █        █" + RESET);
        Bukkit.getLogger().info(ORANGE + "     █    █████    █    █  █  ████"+BLUE+"  ████   ███   ███   ████  █   █    █    █  █  █  ████  ███" + RESET);
        Bukkit.getLogger().info("");
        Bukkit.getLogger().info(PURPLE +  "  Criador: Gilberto" + RESET);
        Bukkit.getLogger().info(PURPLE +  "  Github: https://github.com/GilbertoKPL/" + RESET);
        Bukkit.getLogger().info("");
    }

}
