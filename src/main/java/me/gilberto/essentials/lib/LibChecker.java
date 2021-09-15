package me.gilberto.essentials.lib;

import me.gilberto.essentials.EssentialsMain;
import me.gilberto.essentials.config.configs.langs.Check;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static me.gilberto.essentials.EssentialsMain.instance;
import static me.gilberto.essentials.EssentialsMain.pluginName;
import static me.gilberto.essentials.config.configs.langs.Check.*;

@SuppressWarnings("deprecation")
public class LibChecker {
    private static final String libloc = EssentialsMain.instance().getDataFolder().getPath() + "/lib";
    public static ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
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
        consoleMessage(Check.startvef.replace("%to%", "lib"));
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
            versionfile = YamlConfiguration.loadConfiguration(dowloader("https://www.dropbox.com/s/34gzmbcs61gbu3d/versionchecker.yml?dl=1"));
            todow = versionfile.getInt("version-lib.size");
            double vc = versionfile.getInt("plugin-version");
            if (vc > Double.parseDouble(instance.getDescription().getVersion())) {
                //check version dev..
            }
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
            consoleMessage(error);
        }
    }
}
