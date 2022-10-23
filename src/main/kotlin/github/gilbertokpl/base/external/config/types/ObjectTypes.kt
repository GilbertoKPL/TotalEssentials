package github.gilbertokpl.base.external.config.types

import github.gilbertokpl.base.external.BasePlugin
import org.simpleyaml.configuration.file.YamlFile

enum class ObjectTypes {
    STRING {
        override fun getValueConfig(yml: YamlFile, value: String, basePlugin: BasePlugin): String? {
            if (yml.get(value) == null) return null
            return basePlugin.getValue().getString(yml, value)
        }

        override fun setValueConfig(yml: YamlFile, value: String): YamlFile? {
            if (yml.get(value) == null) return null
            return yml
        }
    },
    STRING_LIST {
        override fun getValueConfig(yml: YamlFile, value: String, basePlugin: BasePlugin): List<String>? {
            if (yml.get(value) == null) return null
            return basePlugin.getValue().getStringList(yml, value)
        }

        override fun setValueConfig(yml: YamlFile, value: String): YamlFile? {
            if (yml.get(value) == null) return null
            return yml
        }
    },
    BOOLEAN {
        override fun getValueConfig(yml: YamlFile, value: String, basePlugin: BasePlugin): Boolean? {
            if (yml.get(value) == null) return null
            return basePlugin.getValue().getBoolean(yml, value)
        }

        override fun setValueConfig(yml: YamlFile, value: String): YamlFile? {
            if (yml.get(value) == null) return null
            return yml
        }
    },
    INTEGER {
        override fun getValueConfig(yml: YamlFile, value: String, basePlugin: BasePlugin): Int? {
            if (yml.get(value) == null) return null
            return basePlugin.getValue().getInt(yml, value)
        }

        override fun setValueConfig(yml: YamlFile, value: String): YamlFile? {
            if (yml.get(value) == null) return null
            return yml
        }
    };

    abstract fun getValueConfig(yml: YamlFile, value: String, basePlugin: BasePlugin): Any?
    abstract fun setValueConfig(yml: YamlFile, value: String): YamlFile?
}