package me.gilberto.essentials.lib;

import me.gilberto.essentials.EssentialsMain;
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

import static me.gilberto.essentials.EssentialsMain.pluginName;

@SuppressWarnings( "deprecation" )
public class LibChecker {
    public static ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    private static InputStream dowloader(String url) throws IOException {
        URLConnection stream = new URL(url).openConnection();
        stream.connect();
        return stream.getInputStream();
    }
    private static void consoleMessage(String msg) {
        EssentialsMain.instance().getServer().getConsoleSender().sendMessage(msg);
    }
    private static final String libloc = EssentialsMain.instance().getDataFolder().getPath() +"/lib";
    static int todow = 0;
    static int dow = 0;
    static boolean termined = false;
    public static void checkversion() {
        YamlConfiguration versionfile;
        consoleMessage(pluginName + " §eIniciando verificação da lib...");
        HashSet<File> noremove = new HashSet<>();
        exec.scheduleAtFixedRate(() -> {
            if (termined) {
                exec.shutdown();
            }
            else {
                if (todow != 0 && dow != 0) {
                    consoleMessage(pluginName + " §eProgresso: " + (100 - (((todow - dow) * 100) / todow)) + "%");
                }
            }
        }, 1, 1,  TimeUnit.SECONDS);
        try {
            versionfile = YamlConfiguration.loadConfiguration(dowloader("https://www.dropbox.com/s/34gzmbcs61gbu3d/versionchecker.yml?dl=1"));
            todow = versionfile.getInt("version-lib.size");
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
                        }
                        else new File(libloc).mkdirs();
                        try {
                            InputStream filelib = dowloader(versionfile.getString(i.replace("version-lib", "repo")));
                            Files.copy(filelib, lib.toPath());
                            noremove.add(lib);
                            dow += (int) lib.length();
                        } catch (Exception e) {
                            consoleMessage(pluginName + " §cErro ao baixar modulo: " + version + ", por favor reporte ao dono do plugin!");
                        }
                    }
                    else noremove.add(lib);
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
            consoleMessage(pluginName + " §eVerificação completa!");
            termined = true;
        } catch (Exception e) {
            consoleMessage(pluginName + " §cErro na verificação de atualização, pulando...");
        }
    }
}
