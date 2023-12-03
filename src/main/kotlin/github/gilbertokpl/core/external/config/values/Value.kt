package github.gilbertokpl.core.external.config.values

import github.gilbertokpl.core.external.CorePlugin
import org.simpleyaml.configuration.file.YamlFile

class Value(private val corePlugin: CorePlugin) {
    fun getString(source: YamlFile, path: String, color: Boolean = true): String? {
        val value = source.getString(path)
        return if (color && value != null) {
            corePlugin.getColor().rgbHex(null, value).replace("%prefix%", corePlugin.serverPrefix)
        } else value
    }

    fun getStringList(source: YamlFile, path: String, color: Boolean = true): List<String> {
        val list = source.getStringList(path)
        return if (color && list.isNotEmpty()) {
            list.map { corePlugin.getColor().rgbHex(null, it).replace("%prefix%", corePlugin.serverPrefix) }
        } else list
    }

    fun getInt(source: YamlFile, path: String): Int = source.getInt(path)

    fun getBoolean(source: YamlFile, path: String): Boolean = source.getBoolean(path)
}