package github.gilbertokpl.core.external.config.types

import github.gilbertokpl.core.external.CorePlugin
import java.io.File

enum class LangTypes {
    PT_BR {
        override fun getFile(basePlugin: CorePlugin): File {
            return File(basePlugin.langPath, "pt_BR.yml")
        }
    },

    EN_US {
        override fun getFile(basePlugin: CorePlugin): File {
            return File(basePlugin.langPath, "en_US.yml")
        }
    };

    abstract fun getFile(basePlugin: CorePlugin): File
}
