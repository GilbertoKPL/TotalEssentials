package me.gilberto.essentials.config.configs.langs

import me.gilberto.essentials.config.ConfigMain
import org.bukkit.configuration.file.YamlConfiguration

object Kits {
    lateinit var notexist: String
    lateinit var editkittime: String
    fun reload(source1: YamlConfiguration) {
        notexist = ConfigMain.getString(source1, "Kits.notexist",true)
        editkittime = ConfigMain.getString(source1, "Kits.editkittime",true)
    }
}