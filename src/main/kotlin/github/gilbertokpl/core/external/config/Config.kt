package github.gilbertokpl.core.external.config

import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.core.external.config.def.DefaultConfig
import github.gilbertokpl.core.external.config.def.DefaultLang
import github.gilbertokpl.core.internal.config.InternalConfig

class Config(lf: CorePlugin) {
    private val configInstance = InternalConfig(lf)

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