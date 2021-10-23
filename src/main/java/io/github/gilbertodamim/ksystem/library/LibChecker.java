package io.github.gilbertodamim.ksystem.library;

import io.github.gilbertodamim.ksystem.KSystemMain;
import io.github.gilbertodamim.ksystem.config.langs.StartLang;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import static io.github.gilbertodamim.ksystem.KSystemMain.instance;
import static io.github.gilbertodamim.ksystem.KSystemMain.pluginName;

@SuppressWarnings({"all"})
public class LibChecker {
    private static final String libloc = KSystemMain.instance().getDataFolder().getPath() + "/lib";
    public static ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    public static boolean update = false;
    static int todow = 0;
    static int dow = 0;
    static boolean termined = false;

    private static InputStream dowloader(String url) throws IOException {
        URLConnection stream = new URL(url).openConnection();
        stream.connect();
        return stream.getInputStream();
    }

    private static void consoleMessage(String msg) {
        KSystemMain.instance().getServer().getConsoleSender().sendMessage(pluginName + " " + msg);
    }

    public static void checkversion() {
        YamlConfiguration versionfile;
        consoleMessage(StartLang.startVerification.replace("%to%", "lib e plugin"));
        HashSet<File> noremove = new HashSet<>();
        exec.scheduleAtFixedRate(() -> {
            if (termined) {
                exec.shutdown();
            } else {
                if (todow != 0 && dow != 0) {
                    consoleMessage(StartLang.download.replace("%perc%", "" + (100 - (((todow - dow) * 100) / todow))));
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
        try {
            File checkfile = new File(libloc, "filecheck.yml");
            new File(libloc).mkdirs();
            Files.copy(dowloader("https://pastebin.com/raw/Nm30fsTp"), checkfile.toPath());
            versionfile = YamlConfiguration.loadConfiguration(checkfile);
            checkfile.delete();
            double pluginv = Double.parseDouble(instance.getDescription().getVersion());
            todow = versionfile.getInt("version-lib-" + pluginv + ".size");
            String vc = versionfile.getString("plugin-version");
            assert vc != null;
            if (Double.parseDouble(vc) > pluginv) {
                consoleMessage(StartLang.updatePlugin.replace("%version%", "" + vc));
                try {
                    InputStream filelib = dowloader(versionfile.getString("plugin-repo"));
                    File pl = new File(instance.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                    File plplace = new File(instance.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().toString().replace(pl.getName(), "KSystem-" + vc + ".jar").replace("file:", ""));
                    Files.copy(filelib, plplace.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    update = true;
                    try {
                        KSystemMain.disablePlugin();
                    } finally {
                        pl.delete();
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "restart");
                    }
                } catch (Exception ex) {
                    consoleMessage(StartLang.anyError);
                }
            }
            if (update) return;
            for (String libs : Objects.requireNonNull(versionfile.getConfigurationSection("version-lib-" + pluginv)).getKeys(false)) {
                if (Objects.equals(libs, "size")) continue;
                String[] split = Objects.requireNonNull(versionfile.getString("version-lib-" + pluginv + "." + libs)).split(":-:");
                String version = split[0];
                File lib = new File(libloc, version + ".jar");
                if (!lib.exists()) {
                    String[] paste = new File(libloc).list();
                    if (paste != null) {
                        for (String d : paste) {
                            String[] e = d.split("-");
                            for (String g : e) {
                                if (g.split("\\.").length > 1) {
                                    if (d.replace("-" + g, "").equals(libs.replace("version-lib-" + pluginv + ".", ""))) {
                                        new File(libloc, d).delete();
                                    }
                                }
                            }
                        }
                    } else new File(libloc).mkdirs();
                    try {
                        InputStream filelib = dowloader(split[1]);
                        Files.copy(filelib, lib.toPath());
                        noremove.add(lib);
                        dow += (int) lib.length();
                    } catch (Exception e) {
                        consoleMessage(StartLang.moduleError.replace("%file%", version));
                        e.printStackTrace();
                    }
                } else {
                    if (lib.length() != Integer.parseInt(split[2])) {
                        try {
                            InputStream filelib = dowloader(split[1]);
                            Files.copy(filelib, lib.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            noremove.add(lib);
                            dow += filelib.available();
                        } catch (Exception e) {
                            consoleMessage(StartLang.moduleError.replace("%file%", version));
                            e.printStackTrace();
                        }
                    } else {
                        noremove.add(lib);
                        dow += (int) lib.length();
                    }
                }
            }
            String[] paste = new File(libloc).list();
            if (paste != null) {
                for (String i : paste) {
                    File a = new File(libloc, i);
                    if (!noremove.contains(a)) {
                        a.delete();
                    } else {
                        Agent.addClassPath(a);
                    }
                }
            }
            consoleMessage(StartLang.completeVerification);
            termined = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}