package github.gilbertokpl.base.external.config

import github.gilbertokpl.base.external.BasePlugin
import github.gilbertokpl.base.external.config.def.DefaultConfig
import github.gilbertokpl.base.external.config.def.DefaultLang
import github.gilbertokpl.base.internal.config.InternalConfig

class Config(lf: BasePlugin) {
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