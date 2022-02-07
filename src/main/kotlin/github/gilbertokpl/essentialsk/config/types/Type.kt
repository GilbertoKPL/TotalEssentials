package github.gilbertokpl.essentialsk.config.types

import github.gilbertokpl.essentialsk.config.get.GetValues
import org.simpleyaml.configuration.file.YamlFile

enum class Type {
    STRING {
        override fun getValueConfig(yml: YamlFile, value: String): String? {
            if (yml.get(value) == null) return null
            return GetValues.getString(yml, value)
        }

        override fun setValueConfig(yml: YamlFile, value: String): YamlFile? {
            if (yml.get(value) == null) return null
            return yml
        }
    },
    STRING_LIST {
        override fun getValueConfig(yml: YamlFile, value: String): List<String>? {
            if (yml.get(value) == null) return null
            return GetValues.getStringList(yml, value)
        }

        override fun setValueConfig(yml: YamlFile, value: String): YamlFile? {
            if (yml.get(value) == null) return null
            return yml
        }
    },
    BOOLEAN {
        override fun getValueConfig(yml: YamlFile, value: String): Boolean? {
            if (yml.get(value) == null) return null
            return GetValues.getBoolean(yml, value)
        }

        override fun setValueConfig(yml: YamlFile, value: String): YamlFile? {
            if (yml.get(value) == null) return null
            return yml
        }
    },
    INTEGER {
        override fun getValueConfig(yml: YamlFile, value: String): Int? {
            if (yml.get(value) == null) return null
            return GetValues.getInt(yml, value)
        }

        override fun setValueConfig(yml: YamlFile, value: String): YamlFile? {
            if (yml.get(value) == null) return null
            return yml
        }
    };

    abstract fun getValueConfig(yml: YamlFile, value: String): Any?

    abstract fun setValueConfig(yml: YamlFile, value: String): YamlFile?
}
