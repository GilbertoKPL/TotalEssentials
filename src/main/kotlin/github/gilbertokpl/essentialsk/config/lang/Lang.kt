package github.gilbertokpl.essentialsk.config.lang

import github.gilbertokpl.essentialsk.util.MainUtil
import java.io.File

enum class Lang {
    PT_BR {
        override fun getFile(): File {
            return File(MainUtil.langPath, "pt_BR.yml")
        }
    },

    EN_US {
        override fun getFile(): File {
            return File(MainUtil.langPath, "en_US.yml")
        }
    };

    abstract fun getFile(): File
}