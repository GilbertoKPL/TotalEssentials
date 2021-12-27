package github.gilbertokpl.essentialsk.util

import com.google.gson.JsonParser
import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.StartLang
import github.gilbertokpl.essentialsk.manager.IInstance
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption


class VersionUtil {
    fun check(): Boolean {
        try {
            PluginUtil.getInstance()
                .consoleMessage(StartLang.getInstance().startVerification.replace("%to%", "version of plugin"))

            val checkJson =
                JsonParser.parseString(IOUtils.toString(URL("https://pastebin.com/raw/GbxhP7qM"), "UTF-8")).asJsonObject

            var versionJson = checkJson.get("version").asString

            if (checkJson.get("version").asString.split(".").size == 2) {
                versionJson = checkJson.get("version").asString + ".0"
            }

            val logger = checkJson.get("logger").asString

            var versionPlugin = ManifestUtil.getInstance().getManifestValue("Plugin-Version") ?: return false

            if (versionPlugin.split(".").size == 2) {
                versionPlugin = checkJson.get("version").asString + ".0"
            }

            if (versionJson.replace(".", "").toInt() > versionPlugin.replace(".", "").toDouble()) {
                PluginUtil.getInstance()
                    .consoleMessage(StartLang.getInstance().updatePlugin.replace("%version%", versionJson))

                val newJar =
                    PluginUtil.getInstance().fileDownloader(checkJson.get("download").asString) ?: return false

                val pluginPath = File(PluginUtil.getInstance().pluginPath)

                var pluginRealPath = File(
                    pluginPath.path.replace(
                        pluginPath.name,
                        "${ManifestUtil.getInstance().getManifestValue("Plugin-Name")}-$versionJson.jar"
                    )
                )

                if (pluginPath.name == pluginRealPath.name) {
                    pluginRealPath = File(pluginPath.path.replace(".jar", "-debug.jar"))
                }

                Files.copy(newJar, pluginRealPath.toPath(), StandardCopyOption.REPLACE_EXISTING)

                PluginUtil.getInstance().consoleMessage(StartLang.getInstance().updatePluginMessage)

                try {
                    PluginUtil.getInstance().unloadPlugin(EssentialsK.instance)
                } finally {
                    pluginPath.delete()
                    EssentialsK.instance.server.dispatchCommand(EssentialsK.instance.server.consoleSender, "restart")
                }
                return true
            } else {
                FileLoggerUtil.getInstance().logger = logger
            }
            PluginUtil.getInstance().consoleMessage(StartLang.getInstance().completeVerification)
            return false
        } catch (ex: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
        }
        return false
    }

    companion object : IInstance<VersionUtil> {
        private val instance = createInstance()
        override fun createInstance(): VersionUtil = VersionUtil()
        override fun getInstance(): VersionUtil {
            return instance
        }
    }
}