package github.gilbertokpl.base.external.config.types

import github.gilbertokpl.base.external.BasePlugin
import java.io.File

enum class LangTypes {
    PT_BR {
        override fun getFile(basePlugin: BasePlugin): File {
            return File(basePlugin.langPath, "pt_BR.yml")
        }
    },

    EN_US {
        override fun getFile(basePlugin: BasePlugin): File {
            return File(basePlugin.langPath, "en_US.yml")
        }
    };

    abstract fun getFile(basePlugin: BasePlugin): File
}
