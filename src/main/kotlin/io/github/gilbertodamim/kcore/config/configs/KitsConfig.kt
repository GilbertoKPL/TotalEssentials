package io.github.gilbertodamim.kcore.config.configs

import io.github.gilbertodamim.kcore.config.ConfigMain
import org.bukkit.configuration.file.YamlConfiguration

object KitsConfig {
    var useShortTime: Boolean = false
    var activated: Boolean = true
    fun reload(source1: YamlConfiguration) {
        useShortTime = ConfigMain.getBoolean(source1, "Kits.use-short-time")
        activated = ConfigMain.getBoolean(source1, "Kits.activated")
    }
}