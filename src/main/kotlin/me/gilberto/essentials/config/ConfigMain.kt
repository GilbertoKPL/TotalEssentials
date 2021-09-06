package me.gilberto.essentials.config

import me.gilberto.essentials.EssentialsInstance.instance
import me.gilberto.essentials.management.Manager.pluginpastedir
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.Files


object ConfigMain {
    lateinit var econf: YamlConfiguration
    fun startconfig() {
        copyconfig("GD_EssentialsConfig")
        econf = YamlConfiguration.loadConfiguration(File(pluginpastedir(), "GD_EssentialsConfig.yml"))
        ConfigLoad()
    }

    private fun copyconfig(source: String) {
        val configfile = File(pluginpastedir(), "$source.yml")
        val resource = instance.javaClass.getResourceAsStream("/$source.yml") ?: return
        if (configfile.exists()) {
            val v = YamlConfiguration.loadConfiguration(File(pluginpastedir(), "$source.yml")).getDouble("Version-file")
            val checkfile = File(pluginpastedir(), "$source-check.yml")
            Files.copy(resource, checkfile.toPath())
            val vc = YamlConfiguration.loadConfiguration(checkfile).getDouble("Version-file")
            if (vc > v) {
                ConfigVersionChecker(configfile, checkfile)
            }
            else {
                checkfile.delete()
            }
            return
        }
        else (File(pluginpastedir()).mkdirs())
        Files.copy(resource, configfile.toPath())
        return
    }

    fun getString(source: YamlConfiguration, path: String): String = source.getString(path)
    fun getStringList(source: YamlConfiguration, path: String): List<String> = source.getStringList(path)
    fun getInt(source: YamlConfiguration, path: String): Int = source.getInt(path)
    fun getIntList(source: YamlConfiguration, path: String): List<Int> = source.getIntegerList(path)

}