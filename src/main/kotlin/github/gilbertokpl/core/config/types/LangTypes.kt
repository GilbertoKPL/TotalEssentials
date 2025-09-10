package github.gilbertokpl.core.config.types

import github.gilbertokpl.core.TotalCore
import java.io.File

enum class LangTypes {
    PT_BR {
        override fun getFile(totalCore: TotalCore): File {
            return File(totalCore.langPath, "pt_BR.yml")
        }
    },

    EN_US {
        override fun getFile(totalCore: TotalCore): File {
            return File(totalCore.langPath, "en_US.yml")
        }
    };

    abstract fun getFile(totalCore: TotalCore): File
}
