package io.github.gilbertodamim.essentials.config.langs

import io.github.gilbertodamim.essentials.config.ConfigMain
import org.bukkit.configuration.file.YamlConfiguration

object GeneralLang {
    lateinit var onlyPlayerCommand: String
    lateinit var notPerm: String
    fun reload(source1: YamlConfiguration) {
        onlyPlayerCommand = ConfigMain.getString(source1, "General.only-player-command", true)
        notPerm = ConfigMain.getString(source1, "General.not-perm", true)
    }
}