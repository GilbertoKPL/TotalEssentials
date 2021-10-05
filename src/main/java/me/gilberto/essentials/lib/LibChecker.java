package me.gilberto.essentials.lib;

import me.gilberto.essentials.EssentialsMain;
import me.gilberto.essentials.config.configs.langs.Check;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static me.gilberto.essentials.EssentialsMain.instance;
import static me.gilberto.essentials.EssentialsMain.pluginName;
import static me.gilberto.essentials.config.configs.langs.Check.*;

public class LibChecker {
    private static final String libloc = EssentialsMain.instance().getDataFolder().getPath() + "/lib";
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
        EssentialsMain.instance().getServer().getConsoleSender().sendMessage(pluginName + " " + msg);
    }

    public static void checkversion() {
        YamlConfiguration versionfile;
        consoleMessage(Check.startvef.replace("%to%", "lib e plugin"));
        HashSet<File> noremove = new HashSet<>();
        exec.scheduleAtFixedRate(() -> {
            if (termined) {
                exec.shutdown();
            } else {
                if (todow != 0 && dow != 0) {
                    consoleMessage(dowload.replace("%perc%", "" + (100 - (((todow - dow) * 100) / todow))));
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
        try {
            File checkfile = new File(libloc, "filecheck.yml");
            Files.copy(dowloader("https://www.dropbox.com/s/34gzmbcs61gbu3d/versionchecker.yml?dl=1"), checkfile.toPath());
            versionfile = YamlConfiguration.loadConfiguration(checkfile);
            checkfile.delete();
            todow = versionfile.getInt("version-lib.size");
            String vc = versionfile.getString("plugin-version");
            assert vc != null;
            if (Double.parseDouble(vc) > Double.parseDouble(instance.getDescription().getVersion())) {
                consoleMessage(updateplugin.replace("%version%", "" + vc));
                try {
                    InputStream filelib = dowloader(versionfile.getString("repo.plugin-repo"));
                    File pl = new File(instance.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                    File plplace = new File(instance.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().toString().replace(pl.getName(), "EssentialsGD-" + vc + ".jar").replace("file:", ""));
                    Files.copy(filelib, plplace.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    update = true;
                    try {
                        EssentialsMain.disableplugin();
                    } finally {
                        pl.delete();
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "restart");
                    }
                } catch (Exception ex) {
                    consoleMessage(String.valueOf(ex));
                    consoleMessage(error);
                }
            }
            if (update) return;
            for (String i : versionfile.getKeys(true)) {
                String[] a = i.split("\\.");
                if (Objects.equals(a[0], "version-lib") && a.length > 1 && !Objects.equals(a[1], "size")) {
                    String version = versionfile.getString(i);
                    File lib = new File(libloc, version + ".jar");
                    if (!lib.exists()) {
                        String[] paste = new File(libloc).list();
                        if (paste != null) {
                            for (String d : paste) {
                                String[] e = d.split("-");
                                for (String g : e) {
                                    if (g.split("\\.").length > 1) {
                                        if (d.replace("-" + g, "").equals(i.replace("version-lib.", ""))) {
                                            new File(libloc, d).delete();
                                        }
                                    }
                                }
                            }
                        } else new File(libloc).mkdirs();
                        try {
                            InputStream filelib = dowloader(versionfile.getString(i.replace("version-lib", "repo")));
                            Files.copy(filelib, lib.toPath());
                            noremove.add(lib);
                            dow += (int) lib.length();
                        } catch (Exception e) {
                            consoleMessage(errormodule.replace("%file%", version));
                        }
                    } else noremove.add(lib);
                }
            }
            String[] paste = new File(libloc).list();
            if (paste != null) {
                for (String i : paste) {
                    File a = new File(libloc, i);
                    if (!noremove.contains(a)) {
                        a.delete();
                    }
                }
            }
            consoleMessage(completeverf);
            termined = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}