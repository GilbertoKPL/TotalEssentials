package github.gilbertokpl.core.external.config.values

import github.gilbertokpl.core.external.CorePlugin
import org.simpleyaml.configuration.file.YamlFile
import java.util.stream.Collectors

class Value(lf: CorePlugin) {

    private val lunarFrame = lf
    fun getString(source: YamlFile, path: String, color: Boolean = true): String? {
        return if (color) {
            lunarFrame.getColor().rgbHex(null, source.getString(path))
                .replace("%prefix%", lunarFrame.serverPrefix)
        } else source.getString(path)
    }

    fun getStringList(source: YamlFile, path: String, color: Boolean = true): List<String> {
        return if (color) {
            source.getStringList(path).stream()
                .map { to -> lunarFrame.getColor().rgbHex(null, to).replace("%prefix%", lunarFrame.serverPrefix) }
                .collect(Collectors.toList())
        } else source.getStringList(path)
    }

    fun getInt(source: YamlFile, path: String): Int = source.getInt(path)

    fun getBoolean(source: YamlFile, path: String): Boolean = source.getBoolean(path)
}