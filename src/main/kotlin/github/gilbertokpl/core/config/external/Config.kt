package github.gilbertokpl.core.config.external

import github.gilbertokpl.core.CorePlugin
import github.gilbertokpl.core.config.CoreConfig
import github.gilbertokpl.core.config.def.DefaultConfig
import github.gilbertokpl.core.config.def.DefaultLang

class Config(corePlugin: CorePlugin) {
    private val configInstance = CoreConfig(corePlugin)

    fun configs(): DefaultConfig {
        return configInstance.configs
    }

    fun messages(): DefaultLang {
        return configInstance.messages
    }

    fun start(configPackage: String) {
        configInstance.start(configPackage)
    }
}