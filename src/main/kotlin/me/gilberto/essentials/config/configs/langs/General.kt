package me.gilberto.essentials.config.configs.langs

import me.gilberto.essentials.config.ConfigMain
import org.bukkit.configuration.file.YamlConfiguration

object General {
    lateinit var onlyplayercommand: String
    lateinit var notperm: String
    fun reload(source1: YamlConfiguration) {
        onlyplayercommand = ConfigMain.getString(source1, "General.onlyplayercommand", true)
        notperm = ConfigMain.getString(source1, "General.notperm", true)
    }
}