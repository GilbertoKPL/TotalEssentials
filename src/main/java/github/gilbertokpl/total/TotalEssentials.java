package github.gilbertokpl.total;

import github.gilbertokpl.core.CorePlugin;
import github.gilbertokpl.core.utils.ConsoleColorUtil;
import github.gilbertokpl.total.cache.data.PlayerData;
import github.gilbertokpl.total.cache.internal.InternalLoader;
import github.gilbertokpl.total.cache.loop.ClearEntitiesLoop;
import github.gilbertokpl.total.cache.loop.PluginLoop;
import github.gilbertokpl.total.cache.sql.*;
import github.gilbertokpl.total.config.files.LangConfig;
import github.gilbertokpl.total.config.files.MainConfig;
import github.gilbertokpl.total.discord.Discord;
import github.gilbertokpl.total.economy.EconomyHolder;
import github.gilbertokpl.total.filter.Filter;
import github.gilbertokpl.total.util.EnchantUtil;
import github.gilbertokpl.total.util.ServerUtil;
import github.gilbertokpl.total.util.MaterialUtil;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.*;

public class TotalEssentials extends JavaPlugin {

    // ====== Static references ======
    private static TotalEssentials instance;
    private static CorePlugin corePlugin;
    private static Object permission;


    // ====== State flags ======
    private static boolean lowVersion = false;
    private static boolean update = false;
    private static boolean libModify = false;
    final boolean publicVer = true;

    public static String jarPath = null;

    // ====== Getters ======
    public static TotalEssentials getInstance() {
        return instance;
    }

    public static CorePlugin getCore() {
        return corePlugin;
    }

    public static boolean isLowVersion() {
        return lowVersion;
    }

    public static Object getPermission() {
        return permission;
    }

    // =========================================================
    // Plugin Lifecycle
    // =========================================================

    public static String JarPath() {
        if (jarPath != null) {
            try {
                return new File(jarPath).getCanonicalPath();
            } catch (Exception ignored) { }
        }

        CodeSource codeSource = TotalEssentials.class.getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            try {
                return new File(codeSource.getLocation().getPath()).getCanonicalPath();
            } catch (Exception ignored) { }
        }
        return "";
    }

    @Override
    public void onLoad() {
        initUpdateAndDependencies();
        if (update || libModify) {
            getLogger().severe("Restarting to apply changes...");
            Bukkit.shutdown();
            return;
        }

        instance = this;
        corePlugin = new CorePlugin(this);

        MaterialUtil.INSTANCE.startMaterials();
        EnchantUtil.INSTANCE.startEnchantments();
        corePlugin.startConfig("github.gilbertokpl.total.config.files");

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
        Runtime runtime = Runtime.getRuntime();

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

    // =========================================================
    // Init Methods (modular but inside same class)
    // =========================================================

    @Override
    public void onDisable() {
        if (update) return;

        savePlaytime();
        ServerUtil.INSTANCE.consoleMessage(ConsoleColorUtil.YELLOW.getColor() + LangConfig.generalSaveDataMessage + ConsoleColorUtil.RESET.getColor());
        corePlugin.stop();
        ServerUtil.INSTANCE.consoleMessage(ConsoleColorUtil.YELLOW.getColor() + LangConfig.generalSaveDataSuccess + ConsoleColorUtil.RESET.getColor());

        TotalEssentials.getCore().getTask().disable();
        if (MainConfig.discordbotConnectDiscordChat) {
            Discord.INSTANCE.sendDiscordMessage(LangConfig.discordchatServerClose, true);
        }
    }

    private void initUpdateAndDependencies() {
        String version;

        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

        try {
            if (publicVer && !isWindows) {
                version = getLatestVersion("GilbertoKPL", "TotalEssentials");
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
            } else {
                version = this.getDescription().getVersion();
            }
        } catch (IOException ignored) {
            version = this.getDescription().getVersion();
        }

        String depend = "https://github.com/GilbertoKPL/TotalEssentials/releases/download/"
                + version + "/TotalEssentials-lib-" + version + ".jar";
        String[] split = depend.split("/");
        String name = split[split.length - 1];

        String pathLib = this.getDataFolder().getPath()
                .replace(".paper-remapped" + File.separator, "")
                + File.separator + "lib" + File.separator;

        String newPath = pathLib + name;

        File file = new File(newPath);
        String classPath = "TotalEssentials/lib/" + name + " ../TotalEssentials/lib/" + name;

        if (!file.exists()) {
            Bukkit.getConsoleSender().sendMessage("§eBaixando dependência = " + name);
            downloadArchive(depend, newPath);
            libModify = true;
        }

        try {
            if ((update && !isWindows) || !Objects.equals(getManifest(), classPath)) {
                modifyManifest(classPath);
                update = true;
            }
        } catch (IOException e) {
            getLogger().severe("§cYou are using Windows. Some features may not work properly!");
        }
    }

    private void startCorePlugin() {
        corePlugin.start(
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
        ServerUtil.INSTANCE.startInventories();
    }

    private void initVersionCheck() {
        if (Bukkit.getBukkitVersion().contains("1.5.2") || Bukkit.getVersion().contains("1.5.2")) {
            lowVersion = true;
        }
    }

    private void initDiscord() {
        JDALogger.setFallbackLoggerEnabled(false);
        Discord.INSTANCE.startBot();
        if (MainConfig.discordbotConnectDiscordChat) {
            Discord.INSTANCE.sendDiscordMessage(LangConfig.discordchatServerStart, true);
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

    // =========================================================
    // Utils (Jar / Network)
    // =========================================================

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

    public static String getManifest() throws IOException {
        try (JarFile jar = new JarFile(JarPath())) {
            Manifest manifest = jar.getManifest();
            return manifest.getMainAttributes().getValue("Class-Path");
        }
    }

    public static void modifyManifest(String classPath) throws IOException {
        String jarFilePath = JarPath();
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            Manifest manifest = jarFile.getManifest();
            Attributes mainAttributes = manifest.getMainAttributes();
            mainAttributes.putValue("Class-Path", classPath);

            Manifest newManifest = new Manifest();
            newManifest.getMainAttributes().putAll(mainAttributes);

            createJarWithNewManifest(jarFilePath, newManifest);
        }
    }

    private static void createJarWithNewManifest(String jarFilePath, Manifest newManifest) throws IOException {
        String tempJarFilePath = Files.createTempFile("temp-totalessentials", "").toString();

        try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tempJarFilePath), newManifest);
             JarFile jarFile = new JarFile(jarFilePath)) {

            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.getName().equals(JarFile.MANIFEST_NAME)) {
                    try (InputStream entryStream = jarFile.getInputStream(entry)) {
                        jarOutputStream.putNextEntry(new JarEntry(entry.getName()));
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = entryStream.read(buffer)) != -1) {
                            jarOutputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }
        }
        Files.move(Paths.get(tempJarFilePath), Paths.get(jarFilePath), StandardCopyOption.REPLACE_EXISTING);
    }

    private void savePlaytime() {
        for (Player p : corePlugin.getReflection().getPlayers()) {
            if (!MainConfig.playtimeActivated) continue;

            long now = System.currentTimeMillis();

            Long timeCache = PlayerData.INSTANCE.getPlayTimeCache().get(p);
            long time = (timeCache != null) ? timeCache : 0L;

            Long startCache = PlayerData.INSTANCE.getPlaytimeLocal().get(p);
            long start = (startCache != null) ? startCache : now;

            long newTime = time + (now - start);

            // Limitar o tempo total sem resetar para valores pequenos
            long maxTime = 94608000000L; // 3 anos em ms
            if (newTime > maxTime) newTime = maxTime;

            PlayerData.INSTANCE.getPlayTimeCache().set(p, newTime);
            PlayerData.INSTANCE.getPlaytimeLocal().set(p, now);
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
}
