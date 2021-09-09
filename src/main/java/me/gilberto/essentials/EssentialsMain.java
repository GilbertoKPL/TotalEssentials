package me.gilberto.essentials;

import me.gilberto.essentials.lib.LibChecker;
import me.gilberto.essentials.management.DisablePlugin;
import me.gilberto.essentials.management.StartPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class EssentialsMain extends JavaPlugin {
    public static EssentialsMain instance;
    public static EssentialsMain instance() {
        return instance;
    }
    public void onEnable() {
        instance = this;
        LibChecker.checkversion();
        new StartPlugin();
        super.onEnable();
    }
    public void onDisable() {
        new DisablePlugin();
        super.onDisable();
    }
}
