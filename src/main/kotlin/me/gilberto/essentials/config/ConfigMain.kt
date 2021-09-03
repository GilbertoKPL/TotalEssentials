package me.gilberto.essentials.config

import me.gilberto.essentials.EssentialsInstance.instance
import me.gilberto.essentials.management.Manager.pluginpastedir
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.Files


object ConfigMain {
    lateinit var econf: YamlConfiguration
    fun startconfig() {
        copyconfig("GD_EssentialsConfig.yml")
        econf = YamlConfiguration.loadConfiguration(File(pluginpastedir(), "GD_EssentialsConfig.yml"))
        ConfigLoad()
    }

    fun reloadconfig() {
        startconfig()
    }

    private fun copyconfig(source: String) {
        val jarPath = File(instance.dataFolder.path, "$source.yml")
        if (jarPath.exists()) return
        val resource = instance.javaClass.getResourceAsStream("/$source.yml")
        if (resource != null) {
            Files.copy(resource, jarPath.toPath())
        }
    }

    fun getString(source: YamlConfiguration, path: String): String = source.getString(path)
    fun getStringList(source: YamlConfiguration, path: String): List<String> = source.getStringList(path)
    fun getInt(source: YamlConfiguration, path: String): Int = source.getInt(path)
    fun getIntList(source: YamlConfiguration, path: String): List<Int> = source.getIntegerList(path)

}