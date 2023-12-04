package github.gilbertokpl.total;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import github.gilbertokpl.core.external.CorePlugin;
import github.gilbertokpl.total.cache.internal.InternalLoader;
import github.gilbertokpl.total.cache.local.PlayerData;
import github.gilbertokpl.total.cache.loop.ClearItemsLoop;
import github.gilbertokpl.total.cache.loop.PluginLoop;
import github.gilbertokpl.total.cache.sql.*;
import github.gilbertokpl.total.config.files.LangConfig;
import github.gilbertokpl.total.config.files.MainConfig;
import github.gilbertokpl.total.discord.Discord;
import github.gilbertokpl.total.util.*;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.*;

public class TotalEssentialsJava extends JavaPlugin {

    public static TotalEssentialsJava instance;
    public static CorePlugin basePlugin;
    public static Permission permission;
    public static boolean lowVersion = false;
    private final List<String> dependency = Collections.singletonList(
            "https://github.com/GilbertoKPL/TotalEssentials/releases/download/1.1.1/TotalEssentials-lib-1.1.1.jar"
    );

    public static TotalEssentialsJava getInstance() {
        return instance;
    }

    public static CorePlugin getBasePlugin() {
        return basePlugin;
    }

    public static Permission getPermission() {
        return permission;
    }

    public static boolean isLowVersion() {
        return lowVersion;
    }

    public static Boolean start = false;

    @Override
    public void onLoad() {

        //lib Checker

        StringBuilder classPath = new StringBuilder();

        for (String depend : dependency) {
            String[] split = depend.split("/");
            String name = split[split.length - 1];
            String path = "plugins/TotalEssentials/lib/";
            String newPath = path + name;

            File file = new File(newPath);

            classPath.append(newPath.replace("plugins/", "")).append(" ");

            if (!file.exists()) {
                Bukkit.getServer().getConsoleSender().sendMessage("Baixando dependencia = %dependency%.".replace("%dependency%", name));
                downloadArchive(depend, newPath);
            }

        }

        try {
            if (!Objects.equals(getManifest(), classPath.toString().toString())) {
                modifyManifest(classPath.toString().toString());
                Bukkit.getServer().getConsoleSender().sendMessage("Reninciando para aplicar modificações");
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
                start = true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //update

        try {
            String version = getLatestVersion("GilbertoKPL", "TotalEssentials");

            if (!Objects.equals(version, this.getDescription().getVersion())) {
               System.out.println("Existe uma nova versão disponivel = " + version + " baixando...");
                boolean archive = downloadArchive("https://github.com/GilbertoKPL/TotalEssentials/releases/download/"+ version +"/TotalEssentials-" + version +".jar", "plugins/TotalEssentials-" + version +".jar");

                if (archive) {
                    new File("plugins/TotalEssentials-" + this.getDescription().getVersion() +".jar").deleteOnExit();
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
                    start = true;
                    System.out.println("Reninciando para aplicar configurações!");
                }
            }

        } catch (IOException ignored) {}


        instance = this;
        basePlugin = new CorePlugin(this);

        MaterialUtil.INSTANCE.startMaterials();

        basePlugin.startConfig("github.gilbertokpl.total.config.files");

        if (MainConfig.moneyActivated) {
            Bukkit.getServicesManager().register(Economy.class, new EconomyHolder(), instance, ServicePriority.Highest);
        }
    }

    @Override
    public void onEnable() {

        if (start) return;

        basePlugin.start(
                "github.gilbertokpl.total.commands",
                "github.gilbertokpl.total.listeners",
                "github.gilbertokpl.total.cache.local",
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

        permission = Bukkit.getServer().getServicesManager().getRegistration(Permission.class).getProvider();

        InternalLoader.INSTANCE.start(
                MainConfig.announcementsListAnnounce,
                LangConfig.deathmessagesCauseReplacer,
                LangConfig.deathmessagesEntityReplacer
        );

        MainUtil.INSTANCE.startInventories();

        if (Bukkit.getBukkitVersion().contains("1.5.2") || Bukkit.getVersion().contains("1.5.2")) {
            lowVersion = true;
        }

        Discord.INSTANCE.startBot();

        if (MainConfig.discordbotConnectDiscordChat) {
            Discord.INSTANCE.sendDiscordMessage(LangConfig.discordchatServerStart, true);
        }

        ClearItemsLoop.INSTANCE.start();

        PluginLoop.INSTANCE.start();

        instance.getServer().getLogger().setFilter(new Filter());
    }

    @Override
    public void onDisable() {

        if (start) return;

        for (Player p : basePlugin.getReflection().getPlayers()) {
            if (MainConfig.playtimeActivated) {
                if (PlayerData.INSTANCE.getPlayTimeCache().get(p) != null) {
                    long time = PlayerData.INSTANCE.getPlayTimeCache().get(p) != null ? PlayerData.INSTANCE.getPlayTimeCache().get(p) : 0L;
                    long timePlayed = PlayerData.INSTANCE.getPlaytimeLocal().get(p) != null ? PlayerData.INSTANCE.getPlayTimeCache().get(p) : 0L;

                    long newTime = time + (System.currentTimeMillis() - timePlayed);

                    if (time > 94608000000L) {
                        newTime = time;
                    }

                    if (newTime > 94608000000L) {
                        newTime = 518400000L;
                    }

                    PlayerData.INSTANCE.getPlayTimeCache().set(p, newTime);
                } else {
                    PlayerData.INSTANCE.getPlayTimeCache().set(p, 0L);
                }
                PlayerData.INSTANCE.getPlaytimeLocal().set(p, 0L);
            }
        }

        MainUtil.INSTANCE.consoleMessage(ColorUtil.YELLOW.getColor() + LangConfig.generalSaveDataMessage + ColorUtil.RESET.getColor());
        basePlugin.stop();
        MainUtil.INSTANCE.consoleMessage(ColorUtil.YELLOW.getColor() + LangConfig.generalSaveDataSuccess + ColorUtil.RESET.getColor());

        TaskUtil.INSTANCE.disable();

        if (MainConfig.discordbotConnectDiscordChat) {
            Discord.INSTANCE.sendDiscordMessage(LangConfig.discordchatServerClose, true);
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

                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                }
            }

            return true;
        } catch (IOException e) {
            e.fillInStackTrace();
            return false;
        }

    }

    public static String getManifest() throws IOException {
        String originalJarPath = JarPath();
        JarFile jar = new JarFile(originalJarPath);
        Manifest originalManifest = jar.getManifest();

        return originalManifest.getMainAttributes().getValue("Class-Path");
    }

    public static void modifyManifest(String ClassPath) throws IOException {
        String jarFilePath = JarPath();
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            Manifest manifest = jarFile.getManifest();

            if (manifest != null) {
                Attributes mainAttributes = manifest.getMainAttributes();
                mainAttributes.remove("Class-Path");
                mainAttributes.putValue("Class-Path", ClassPath);
                Manifest newManifest = new Manifest();
                newManifest.getMainAttributes().putAll(mainAttributes);
                createJarWithNewManifest(jarFilePath, newManifest);
            } else {
                throw new IOException();
            }
        }
    }

    private static void createJarWithNewManifest(String jarFilePath, Manifest newManifest) throws IOException {
        String tempJarFilePath = Files.createTempFile("teste", "").toString();

        try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tempJarFilePath), newManifest)) {
            try (JarFile jarFile = new JarFile(jarFilePath)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (!entry.getName().equals(JarFile.MANIFEST_NAME)) {
                        try (InputStream entryStream = jarFile.getInputStream(entry)) {
                            jarOutputStream.putNextEntry(new JarEntry(entry.getName()));

                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = entryStream.read(buffer)) != -1) {
                                jarOutputStream.write(buffer, 0, bytesRead);
                            }
                        }
                    }
                }
            }
        }
        Files.move(Paths.get(tempJarFilePath), Paths.get(jarFilePath), StandardCopyOption.REPLACE_EXISTING);
    }

    public static String JarPath() {
        Class<?> currentClass = TotalEssentialsJava.class;
        CodeSource codeSource = currentClass.getProtectionDomain().getCodeSource();

        if (codeSource != null) {
            URL location = codeSource.getLocation();
            String path = location.getPath();
            try {
                path = new File(path).getCanonicalPath();
            } catch (Exception e) {
                e.fillInStackTrace();
            }

            return path;
        } else {
            return "";
        }
    }

    public static String getLatestVersion(String owner, String repo) throws IOException {
        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest";
        URL url = new URL(apiUrl);

        try (InputStream inputStream = url.openConnection().getInputStream()) {
            String jsonContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            JsonObject json = JsonParser.parseString(jsonContent).getAsJsonObject();

            return json.get("tag_name").getAsString();
        }
    }

}
