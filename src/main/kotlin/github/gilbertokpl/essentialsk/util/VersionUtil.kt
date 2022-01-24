package github.gilbertokpl.essentialsk.util

import com.google.gson.JsonParser
import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.StartLang
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

internal object VersionUtil {
    fun check(): Boolean {
        try {
            MainUtil
                .consoleMessage(StartLang.startVerification.replace("%to%", "version of plugin"))

            val checkJson =
                JsonParser.parseString(IOUtils.toString(URL("https://pastebin.com/raw/GbxhP7qM"), "UTF-8")).asJsonObject

            var versionJson = checkJson.get("version").asString

            if (checkJson.get("version").asString.split(".").size == 2) {
                versionJson = checkJson.get("version").asString + ".0"
            }

            val logger = checkJson.get("logger").asString

            var versionPlugin = ManifestUtil.getManifestValue("Plugin-Version") ?: return false

            val split = versionPlugin.split("-")

            var snapshot = false

            if (split.size == 2) {
                snapshot = true
                versionPlugin = split[0]
            }

            var versionPluginInt = versionPlugin.replace(".", "").toDouble()

            if (snapshot) {
                versionPluginInt -= 1
            }

            if (versionJson.replace(".", "").toInt() > versionPluginInt) {
                MainUtil
                    .consoleMessage(StartLang.updatePlugin.replace("%version%", versionJson))

                val newJar =
                    MainUtil.fileDownloader(checkJson.get("download").asString) ?: return false

                val pluginPath = File(MainUtil.pluginPath)

                var pluginRealPath = File(
                    pluginPath.path.replace(
                        pluginPath.name,
                        "${ManifestUtil.getManifestValue("Plugin-Name")}-$versionJson.jar"
                    )
                )

                if (pluginPath.name == pluginRealPath.name) {
                    pluginRealPath = File(pluginPath.path.replace(".jar", "-debug.jar"))
                }

                Files.copy(newJar, pluginRealPath.toPath(), StandardCopyOption.REPLACE_EXISTING)

                MainUtil.consoleMessage(StartLang.updatePluginMessage)

                try {
                    try {
                        PluginUtil.unload(EssentialsK.instance)
                    } finally {
                        pluginPath.delete()
                        EssentialsK.instance.server.dispatchCommand(
                            EssentialsK.instance.server.consoleSender,
                            "restart"
                        )
                    }
                } catch (e: Throwable) {
                    pluginPath.delete()
                    EssentialsK.instance.server.dispatchCommand(
                        EssentialsK.instance.server.consoleSender,
                        "restart"
                    )
                }
                return true
            } else {
                FileLoggerUtil.logger = logger
            }
            MainUtil.consoleMessage(StartLang.completeVerification)
            return false
        } catch (ex: Throwable) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
        }
        return false
    }
}
