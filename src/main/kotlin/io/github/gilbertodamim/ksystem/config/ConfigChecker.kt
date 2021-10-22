package io.github.gilbertodamim.ksystem.config

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class ConfigChecker(file: File, newfile: File, lang: Boolean) {
    private val conffile = YamlConfiguration.loadConfiguration(file)
    private val confnewfile = YamlConfiguration.loadConfiguration(newfile)

    init {
        var delete = true
        for (fileKeys in confnewfile.getKeys(true)) {
            if (conffile.get(fileKeys) == null) {
                ConfigVersionChecker(file, newfile, null, lang)
                delete = false
                break
            }
        }
        if (delete) {
            for (fileKeys in conffile.getKeys(true)) {
                if (confnewfile.get(fileKeys) == null) {
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