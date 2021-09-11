package me.gilberto.essentials.config

import me.gilberto.essentials.config.configs.Database
import me.gilberto.essentials.config.configs.lang

class ConfigReload {
    init {
        Database.reload()
        lang.reload()
    }
}