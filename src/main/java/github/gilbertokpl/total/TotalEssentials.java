package github.gilbertokpl.total;

import github.gilbertokpl.core.TotalCore;
import github.gilbertokpl.core.utils.ConsoleColorUtil;
import github.gilbertokpl.total.cache.internal.InternalLoader;
import github.gilbertokpl.total.cache.loop.ClearEntitiesLoop;
import github.gilbertokpl.total.cache.loop.PluginLoop;
import github.gilbertokpl.total.cache.sql.*;
import github.gilbertokpl.total.config.files.LangConfig;
import github.gilbertokpl.total.config.files.MainConfig;
import github.gilbertokpl.total.discord.DiscordManager;
import github.gilbertokpl.total.economy.EconomyHolder;
import github.gilbertokpl.total.filter.Filter;
import github.gilbertokpl.total.util.*;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class TotalEssentials extends JavaPlugin {

    // ====== Static references ======
    private static TotalEssentials instance;
    private static TotalCore totalCore;
    private static Object permission;


    // ====== State flags ======
    public static boolean lowVersion = false;
    public static boolean update = false;
    public final boolean publicVer = false;
    public static String jarPath = null;

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
        initUpdateCheck();
        if (update) {
            getLogger().severe("Restarting to apply changes...");
            Bukkit.shutdown();
            return;
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
        if (update) return;

        printConsoleBanner();

        Runtime runtime = Runtime.getRuntime();

        EnchantUtil.INSTANCE.startEnchantments();

        long before = runtime.totalMemory() - runtime.freeMemory();

        startCorePlugin();

        long after = runtime.totalMemory() - runtime.freeMemory();

        initPermissions();
        initLoaders();
        initVersionCheck();
        initDiscord();
        initLoops();
        instance.getServer().getLogger().setFilter(new Filter());

        getLogger().info("The cache is using approximately: " + ((after - before) / 1024 / 1024) + " MB");
    }

    @Override
    public void onDisable() {
        if (update) return;

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

    public void initUpdateCheck() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

        try {
            if (publicVer && !isWindows) {
                String version = getLatestVersion("GilbertoKPL", "TotalEssentials");
                if (!Objects.equals(version, this.getDescription().getVersion())) {
                    getLogger().severe("§eNew version available = " + version + ", downloading...");
                    jarPath = "plugins" + File.separator + "TotalEssentials-" + version + ".jar";

                    boolean archive = downloadArchive(
                            "https://github.com/GilbertoKPL/TotalEssentials/releases/download/"
                                    + version + "/TotalEssentials-" + version + ".jar",
                            jarPath
                    );

                    if (archive) {
                        new File("plugins" + File.separator + "TotalEssentials-" + this.getDescription().getVersion() + ".jar")
                                .deleteOnExit();
                        update = true;
                    }
                }
            }
        } catch (IOException ignored) {
        }
    }

    public static boolean downloadArchive(String urlDownload, String path) {
        try {
            URL url = new URL(urlDownload);
            URLConnection connection = url.openConnection();

            try (InputStream inputStream = connection.getInputStream()) {
                File localArchive = new File(path);
                localArchive.getParentFile().mkdirs();

                try (FileOutputStream outputStream = new FileOutputStream(localArchive)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String getLatestVersion(String owner, String repo) throws IOException {
        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest";
        URL url = new URL(apiUrl);

        try (InputStream inputStream = url.openStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            int startIndex = jsonContent.indexOf("\"tag_name\":\"") + 12;
            int endIndex = jsonContent.indexOf("\"", startIndex);
            return jsonContent.substring(startIndex, endIndex);
        }
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
