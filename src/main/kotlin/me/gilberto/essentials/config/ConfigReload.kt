package me.gilberto.essentials.config

import me.gilberto.essentials.config.configs.Database

class ConfigReload {
    init {
        Database.reload()
    }
}