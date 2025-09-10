package github.gilbertokpl.core.config.types

import github.gilbertokpl.core.TotalCore
import org.simpleyaml.configuration.file.YamlFile

enum class ObjectTypes {
    STRING {
        override fun getValueConfig(yml: YamlFile, value: String, totalCore: TotalCore): String? {
            if (yml.get(value) == null) return null
            return totalCore.getValue().getString(yml, value)
        }

        override fun setValueConfig(yml: YamlFile, value: String): YamlFile? {
            if (yml.get(value) == null) return null
            return yml
        }
    },
    STRING_LIST {
        override fun getValueConfig(yml: YamlFile, value: String, totalCore: TotalCore): List<String>? {
            if (yml.get(value) == null) return null
            return totalCore.getValue().getStringList(yml, value)
        }

        override fun setValueConfig(yml: YamlFile, value: String): YamlFile? {
            if (yml.get(value) == null) return null
            return yml
        }
    },
    BOOLEAN {
        override fun getValueConfig(yml: YamlFile, value: String, totalCore: TotalCore): Boolean? {
            if (yml.get(value) == null) return null
            return totalCore.getValue().getBoolean(yml, value)
        }

        override fun setValueConfig(yml: YamlFile, value: String): YamlFile? {
            if (yml.get(value) == null) return null
            return yml
        }
    },
    INTEGER {
        override fun getValueConfig(yml: YamlFile, value: String, totalCore: TotalCore): Int? {
            if (yml.get(value) == null) return null
            return totalCore.getValue().getInt(yml, value)
        }

        override fun setValueConfig(yml: YamlFile, value: String): YamlFile? {
            if (yml.get(value) == null) return null
            return yml
        }
    };

    abstract fun getValueConfig(yml: YamlFile, value: String, totalCore: TotalCore): Any?
    abstract fun setValueConfig(yml: YamlFile, value: String): YamlFile?
}