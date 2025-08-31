package github.gilbertokpl.core.config.types

import github.gilbertokpl.core.CorePlugin
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
