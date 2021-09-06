package me.gilberto.essentials.config

import me.gilberto.essentials.EssentialsInstance.instance
import me.gilberto.essentials.management.Manager.consoleMessage
import me.gilberto.essentials.management.Manager.disableplugin
import me.gilberto.essentials.management.Manager.pluginpastedir
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.Files


object ConfigMain {
    var configs = HashSet<YamlConfiguration>()
    lateinit var econf: YamlConfiguration
    fun startconfig() {
        econf = copyconfig("GD_EssentialsConfig") ?: return
        ConfigLoad()
    }
    fun addtoconfig (source: YamlConfiguration, path: String, value: Any) {
        source.addDefault(path, value)
        reloadconfig()
    }

    fun reloadconfig() {
        fun test(source: YamlConfiguration) {
            val check: YamlConfiguration
            try {
                check = YamlConfiguration.loadConfiguration(File(pluginpastedir(), "${source.name}.yml"))
            } catch (Ex: Exception) {
                Ex.printStackTrace()
                consoleMessage("§cProblema na config $source, retornando a antiga!")
                source.save(source.currentPath)
                return
            }
            check.save(source.currentPath)
        }
        for (i in configs) {
            test(i)
        }
    }

    private fun copyconfig(source: String) : YamlConfiguration? {
        val configfile = File(pluginpastedir(), "$source.yml")
        val resource = instance.javaClass.getResourceAsStream("/$source.yml") ?: return YamlConfiguration.loadConfiguration(File(pluginpastedir(), "$source.yml"))
        if (configfile.exists()) {
            val check : YamlConfiguration
            try {
                check = YamlConfiguration.loadConfiguration(File(pluginpastedir(), "$source.yml"))
            }
            catch (Ex: Exception) {
                consoleMessage("§cProblema na config $source.")
                disableplugin()
                Ex.printStackTrace()
                return null
            }
            val v = check.getDouble("Version-file")
            val checkfile = File(pluginpastedir(), "$source-check.yml")
            if (checkfile.exists()) checkfile.delete()
            Files.copy(resource, checkfile.toPath())
            val vc = YamlConfiguration.loadConfiguration(checkfile).getDouble("Version-file")
            if (vc > v) {
                ConfigVersionChecker(configfile, checkfile, vc.toString())
            }
            else {
                ConfigChecker(configfile, checkfile)
            }
            val b = YamlConfiguration.loadConfiguration(File(pluginpastedir(), "$source.yml"))
            configs.add(b)
            return b
        }
        else (File(pluginpastedir()).mkdirs())
        Files.copy(resource, configfile.toPath())
        return YamlConfiguration.loadConfiguration(File(pluginpastedir(), "$source.yml"))
    }

    fun getString(source: YamlConfiguration, path: String): String = source.getString(path)
    fun getStringList(source: YamlConfiguration, path: String): List<String> = source.getStringList(path)
    fun getInt(source: YamlConfiguration, path: String): Int = source.getInt(path)
    fun getIntList(source: YamlConfiguration, path: String): List<Int> = source.getIntegerList(path)

}