package me.gilberto.essentials.lib;

import me.gilberto.essentials.EssentialsMain;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class LibChecker {
    private static InputStream dowloader(String url) throws IOException {
        URLConnection stream = new URL(url).openConnection();
        stream.connect();
        return stream.getInputStream();
    }
    private static void consoleMessage(String msg) {
        EssentialsMain.instance().getServer().getConsoleSender().sendMessage(msg);
    }
    private static final String libloc = EssentialsMain.instance().getDataFolder().getPath() +"/lib";
    public static void checkversion() {
        YamlConfiguration versionfile;
        consoleMessage("§aIniciando verificação da lib");
        HashSet<File> noremove = new HashSet<>();
        try {
            versionfile = YamlConfiguration.loadConfiguration(dowloader("https://www.dropbox.com/s/34gzmbcs61gbu3d/versionchecker.yml?dl=1"));
        } catch (Exception e) {
            consoleMessage("§cErro ao iniciar verificação de atualização!");
            return;
        }
        for (String i : versionfile.getKeys(true)) {
            String[] a = i.split("\\.");
            if (Objects.equals(a[0], "version-lib") && a.length > 1) {
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
                                        boolean deleted = new File(libloc, d).delete();
                                        if (deleted) {
                                            consoleMessage("§cDeletado antiga lib: " + d + ";");
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else new File(libloc).mkdirs();
                    consoleMessage("§aBaixando lib: "+ version + "...");
                    try {
                        InputStream filelib = dowloader(versionfile.getString(i.replace("version-lib", "repo")));
                        consoleMessage("§aBaixado lib: " + version + " com sucesso !");
                        Files.copy(filelib, lib.toPath());
                        noremove.add(lib);
                    } catch (Exception e) {
                        consoleMessage("§cErro ao baixar modulo: " + version + " !");
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
                    boolean deleted = a.delete();
                    if (deleted) {
                        consoleMessage("§cDeletado arquivo que não é da lib: " + i + ";");
                    }
                }
            }
        }
        consoleMessage("§aVerificação completa!");
    }
}
