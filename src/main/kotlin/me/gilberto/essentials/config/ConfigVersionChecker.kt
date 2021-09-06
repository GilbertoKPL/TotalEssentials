package me.gilberto.essentials.config

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class ConfigVersionChecker(file : File, newfile: File) {
    val conffile  = YamlConfiguration.loadConfiguration(file)
    val confnewfile = YamlConfiguration.loadConfiguration(newfile)
    init {
        for (i in confnewfile.getKeys(true)) {
            if (i == "Version-file") continue
            conffile.addDefault(i, confnewfile.get(i))
        }
        conffile.save(file)
        newfile.delete()
    }
}