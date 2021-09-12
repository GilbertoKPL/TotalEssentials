package me.gilberto.essentials.config

import me.gilberto.essentials.config.configs.Database
import me.gilberto.essentials.config.configs.Lang

class ConfigReload {
    init {
        Database.reload()
        Lang.reload()
    }
}