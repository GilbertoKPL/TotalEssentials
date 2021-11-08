package io.github.gilbertodamim.kcore.library;

import io.github.gilbertodamim.kcore.KCoreMain;
import io.github.gilbertodamim.kcore.config.ConfigMain;
import io.github.gilbertodamim.kcore.config.langs.StartLang;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.github.gilbertodamim.kcore.KCoreMain.instance;
import static io.github.gilbertodamim.kcore.KCoreMain.pluginTagName;

@SuppressWarnings({"all"})
public class LibChecker {
    private static final String libraryLocation = KCoreMain.instance().getDataFolder().getPath() + "/lib";

    public static ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

    public static boolean update = false;

    public static boolean libChecker = false;

    private static int toDownload = 0;

    private static int download = 0;

    private static boolean finished = false;

    private static InputStream downloader(String url) throws IOException {
        URLConnection stream = new URL(url).openConnection();
        stream.connect();
        return stream.getInputStream();
    }

    private static void consoleMessage(String msg) {
        KCoreMain.instance().getServer().getConsoleSender().sendMessage(pluginTagName + " " + msg);
    }

    public static void checkVersion() {
        YamlConfiguration checkFileYaml;
        consoleMessage(StartLang.startVerification.replace("%to%", "lib e plugin"));
        List<File> noRemove = new ArrayList<>();
        exec.scheduleAtFixedRate(() -> {
            if (finished) {
                exec.shutdown();
            } else {
                if (toDownload != 0 && download != 0) {
                    consoleMessage(StartLang.download.replace("%perc%", "" + (100 - (((toDownload - download) * 100) / toDownload))));
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
        try {
            File checkFile = new File(libraryLocation, "filecheck.yml");
            new File(libraryLocation).mkdirs();
            Files.copy(downloader("https://pastebin.com/raw/Nm30fsTp"), checkFile.toPath());
            checkFileYaml = YamlConfiguration.loadConfiguration(checkFile);
            checkFile.delete();
            Double pluginVersion = KCoreMain.version;
            String versionToCheck = checkFileYaml.getString("plugin-version");
            assert versionToCheck != null;
            if (Double.parseDouble(versionToCheck) > pluginVersion) {
                update = true;
                pluginVersion = Double.parseDouble(versionToCheck);
                consoleMessage(StartLang.updatePlugin.replace("%version%", "" + versionToCheck));
            }
            for (String libs : Objects.requireNonNull(checkFileYaml.getConfigurationSection("version-lib-" + pluginVersion)).getKeys(false)) {
                toDownload += Integer.parseInt(Objects.requireNonNull(checkFileYaml.getString("version-lib-" + pluginVersion + "." + libs)).split(":-:")[2]);
            }
            for (String libs : Objects.requireNonNull(checkFileYaml.getConfigurationSection("version-lib-" + pluginVersion)).getKeys(false)) {
                String[] split = Objects.requireNonNull(checkFileYaml.getString("version-lib-" + pluginVersion + "." + libs)).split(":-:");
                String version = split[0];
                File lib = new File(libraryLocation, version + ".jar");
                if (!lib.exists()) {
                    libChecker = true;
                    String[] paste = new File(libraryLocation).list();
                    if (paste != null) {
                        for (String d : paste) {
                            String[] e = d.split("-");
                            for (String g : e) {
                                if (g.split("\\.").length > 1) {
                                    if (d.replace("-" + g, "").equals(libs.replace("version-lib-" + pluginVersion + ".", ""))) {
                                        new File(libraryLocation, d).delete();
                                    }
                                }
                            }
                        }
                    } else new File(libraryLocation).mkdirs();
                    try {
                        InputStream fileLibrary = downloader(split[1]);
                        Files.copy(fileLibrary, lib.toPath());
                        noRemove.add(lib);
                        download += (int) lib.length();
                    } catch (Exception e) {
                        consoleMessage(StartLang.moduleError.replace("%file%", version));
                        e.printStackTrace();
                    }
                } else {
                    if (lib.length() != Integer.parseInt(split[2])) {
                        try {
                            InputStream fileLibrary = downloader(split[1]);
                            Files.copy(fileLibrary, lib.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            noRemove.add(lib);
                            download += fileLibrary.available();
                        } catch (Exception e) {
                            consoleMessage(StartLang.moduleError.replace("%file%", version));
                            e.printStackTrace();
                        }
                    } else {
                        noRemove.add(lib);
                        download += (int) lib.length();
                    }
                }
            }
            String[] paste = new File(libraryLocation).list();
            if (paste != null) {
                for (String i : paste) {
                    File a = new File(libraryLocation, i);
                    if (!noRemove.contains(a)) {
                        a.delete();
                    }
                }
            }
            finished = true;
            if (!update) {
                consoleMessage(StartLang.completeVerification);
            } else {
                try {
                    InputStream fileLibrary = downloader(checkFileYaml.getString("plugin-repo"));
                    File plugin = new File(instance.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                    File pluginToPlace = new File(instance.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().toString().replace(plugin.getName(), "KCore-" + versionToCheck + ".jar").replace("file:", ""));
                    if (plugin.getName().equals(pluginToPlace.getName())) {
                        pluginToPlace = new File(instance.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().toString().replace(plugin.getName(), "KCore-" + versionToCheck + "-debug.jar").replace("file:", ""));
                    }
                    Files.copy(fileLibrary, pluginToPlace.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    try {
                        KCoreMain.disablePlugin();
                    } finally {
                        plugin.delete();
                    }
                } catch (Exception ex) {
                    consoleMessage(StartLang.anyError);
                    ex.printStackTrace();
                }
                consoleMessage(StartLang.updatePluginMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reloadClass(String Path, Class cl, YamlConfiguration source, Boolean color) throws IllegalAccessException {
        for (Field i : cl.getDeclaredFields()) {
            String[] names = i.getName().split("(?=\\p{Upper})");
            String createdPatch = "";
            for (String name : names) {
                if (createdPatch == "") {
                    createdPatch = Path + "." + name.toLowerCase();
                } else {
                    createdPatch += "-" + name.toLowerCase();
                }
            }
            if (i.getGenericType().getTypeName().equalsIgnoreCase("java.util.List<java.lang.String>")) {
                try {
                    i.set(cl, ConfigMain.INSTANCE.getStringList(source, createdPatch, color));
                } catch (NullPointerException e) {
                    consoleMessage("Error in " + i.getName() );
                    e.printStackTrace();
                }
                continue;
            }
            if (i.getGenericType().getTypeName() == "java.lang.String") {
                try {
                    i.set(cl, ConfigMain.INSTANCE.getString(source, createdPatch, color));
                } catch (NullPointerException e) {
                    consoleMessage("Error in " + i.getName() );
                    e.printStackTrace();
                }
                continue;
            }
            if (i.getGenericType().getTypeName() == "java.lang.Boolean") {
                try {
                    i.set(cl, ConfigMain.INSTANCE.getBoolean(source, createdPatch));
                } catch (NullPointerException e) {
                    consoleMessage("Error in " + i.getName() );
                    e.printStackTrace();
                }
                continue;
            }
            if (i.getGenericType().getTypeName() == "java.lang.Integer") {
                try {
                    i.set(cl, ConfigMain.INSTANCE.getInt(source, createdPatch));
                } catch (NullPointerException e) {
                    consoleMessage("Error in " + i.getName() );
                    e.printStackTrace();
                }
                continue;
            }
            consoleMessage(i.getName() + " -" + i.getGenericType().getTypeName() + "- ");
        }
    }
}