package me.gilberto.essentials.config

import me.gilberto.essentials.config.configs.langs.General
import me.gilberto.essentials.config.configs.langs.Kits
import me.gilberto.essentials.config.configs.langs.Time

class LangReload {
    init {
        Kits.reload(ConfigMain.lang)
        Time.reload(ConfigMain.lang)
        General.reload(ConfigMain.lang)
    }
}