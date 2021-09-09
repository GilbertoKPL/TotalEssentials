package me.gilberto.essentials

import me.gilberto.essentials.EssentialsInstance.startInstance
import me.gilberto.essentials.management.DisablePlugin
import me.gilberto.essentials.management.StartPlugin
import org.bukkit.plugin.java.JavaPlugin

class EssentialsMain : JavaPlugin() {
    override fun onEnable() {
        startInstance(this)
        StartPlugin()
        super.onEnable()
    }
    override fun onDisable() {
        DisablePlugin()
        super.onDisable()
    }
}