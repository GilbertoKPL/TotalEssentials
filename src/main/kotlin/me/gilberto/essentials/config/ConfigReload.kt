package me.gilberto.essentials.config

import me.gilberto.essentials.config.ConfigMain.econf
import me.gilberto.essentials.config.configs.Database
import me.gilberto.essentials.config.configs.Kits

class ConfigReload {
    init {
        Database.reload(econf)
        Kits.reload(econf)
    }
}