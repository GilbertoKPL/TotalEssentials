package me.gilberto.essentials.config.configs

import me.gilberto.essentials.config.ConfigMain
import org.bukkit.configuration.file.YamlConfiguration

object Kits {
    var Useshorttime: Boolean = false
    var activated: Boolean = true
    fun reload(source1: YamlConfiguration) {
        Useshorttime = ConfigMain.getBoolean(source1, "Kits.Use-short-time")
        activated = ConfigMain.getBoolean(source1, "Kits.activated")
    }
}