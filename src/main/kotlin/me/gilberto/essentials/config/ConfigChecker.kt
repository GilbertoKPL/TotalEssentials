package me.gilberto.essentials.config

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class ConfigChecker(file: File, newfile: File, lang: Boolean) {
    private val conffile = YamlConfiguration.loadConfiguration(file)
    private val confnewfile = YamlConfiguration.loadConfiguration(newfile)

    init {
        var delete = true
        for (i in confnewfile.getKeys(true)) {
            if (conffile.get(i) == null) {
                ConfigVersionChecker(file, newfile, null, lang)
                delete = false
                break
            }
        }
        if (delete) {
            for (i in conffile.getKeys(true)) {
                if (confnewfile.get(i) == null) {
                    ConfigVersionChecker(file, newfile, null, lang)
                    delete = false
                    break
                }
            }
        }
        if (delete) {
            newfile.delete()
        }
    }
}