package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.configs.StartLang
import github.gilbertokpl.essentialsk.manager.ELang
import github.gilbertokpl.essentialsk.manager.IInstance
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.simpleyaml.configuration.file.YamlFile
import java.io.File
import java.nio.file.*
import java.util.*
import java.util.stream.Collectors


class ConfigUtil {
    private val configList = HashMap<String, YamlFile>()

    private var bol = false

    fun start() {
        PluginUtil.getInstance().consoleMessage(StartLang.getInstance().startVerification.replace("%to%", "config"))

        startFun("configs", false)

        PluginUtil.getInstance().consoleMessage(StartLang.getInstance().completeVerification)

        PluginUtil.getInstance().consoleMessage(StartLang.getInstance().startVerification.replace("%to%", "lang"))

        startFun("langs", true)

        PluginUtil.getInstance().consoleMessage(StartLang.getInstance().completeVerification)
    }

    internal fun getString(source: YamlFile, path: String, color: Boolean = true): String {
        return if (color) {
            source.getString(path)!!.replace("&", "§")
        } else source.getString(path)!!
    }

    internal fun getStringList(source: YamlFile, path: String, color: Boolean = true): List<String> {
        return if (color) {
            source.getStringList(path).stream().map { to -> to.replace("&", "§") }.collect(Collectors.toList())
        } else source.getStringList(path)
    }

    internal fun getInt(source: YamlFile, path: String): Int = source.getInt(path)

    internal fun getBoolean(source: YamlFile, path: String): Boolean = source.getBoolean(path)

    private fun internalReloadConfig() {
        ReflectUtil.getInstance().setValuesFromClass(
            MainConfig::class,
            MainConfig.getInstance(),
            configList.values.toList(),
            File(PluginUtil.getInstance().mainPath, "MainConfig.yml")
        )
    }

    private fun startFun(lc: String, lang: Boolean) {
        val directoryStream: DirectoryStream<Path>? = Files.newDirectoryStream(
            FileSystems.newFileSystem(
                Paths.get(EssentialsK.instance.javaClass.protectionDomain.codeSource.location.toURI()),
                EssentialsK.instance.javaClass.classLoader
            ).getPath("/$lc/")
        )

        if (directoryStream != null) {
            for (i in directoryStream) {
                if (i.fileName.toString() == "MainConfig.yml") {
                    bol = initializeYml(i.fileName.toString().replace(".yml", ""), lang)
                    continue
                }
                initializeYml(i.fileName.toString().replace(".yml", ""), lang)
            }
        }

        if (lang) {
            try {
                if (bol) {
                    val timezone = try {
                        TimeZone.getDefault().id.split("/")[1].uppercase()
                    } catch (e: Exception) {
                        "OTHER"
                    }
                    val enum = try {
                        ELang.valueOf(timezone).locale
                    } catch (e: Exception) {
                        ELang.valueOf("OTHER").locale
                    }
                    MainConfig.getInstance().generalSelectedLang = enum
                    internalReloadConfig()
                }
                var langSelected =
                    File(PluginUtil.getInstance().langPath, "${MainConfig.getInstance().generalSelectedLang}.yml")

                if (langSelected.exists()) {
                    PluginUtil.getInstance().consoleMessage(
                        StartLang.getInstance().langSelectedMessage.replace(
                            "%lang%",
                            "${MainConfig.getInstance().generalSelectedLang}.yml"
                        )
                    )
                } else {
                    langSelected = File(PluginUtil.getInstance().langPath, "pt_BR.yml")
                    PluginUtil.getInstance().consoleMessage(StartLang.getInstance().langError)
                }

                val langYaml = YamlFile(langSelected)
                langYaml.load()

                ReflectUtil.getInstance()
                    .setValuesOfClass(GeneralLang::class, GeneralLang.getInstance(), listOf(langYaml))

            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
            return
        }

        try {
            ReflectUtil.getInstance()
                .setValuesOfClass(MainConfig::class, MainConfig.getInstance(), configList.values.toList())
        } catch (ex: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
        }
    }

    private fun initializeYml(source: String, lang: Boolean = false): Boolean {
        val location: String
        val dir: String
        val message: String
        if (lang) {
            location = "/langs/$source.yml"
            dir = PluginUtil.getInstance().langPath
            message = "lang"
        } else {
            location = "/configs/$source.yml"
            dir = PluginUtil.getInstance().mainPath
            message = "config"
        }
        try {
            val configFile = File(dir, "$source.yml")
            val resource = EssentialsK.instance.javaClass.getResourceAsStream(location)
            if (configFile.exists()) {
                val configYaml = YamlFile(configFile)
                configYaml.loadWithComments()
                if (resource == null) return false
                val tempFile = File.createTempFile("check", ".yml")
                FileUtils.copyToFile(resource, tempFile)

                val tempConfig = YamlFile(tempFile)
                tempConfig.loadWithComments()

                configHelper(
                    tempConfig,
                    configYaml,
                    configFile
                )

                tempFile.delete()

                val finalConfigYaml = YamlFile(configFile)
                finalConfigYaml.loadWithComments()

                configList[source] = finalConfigYaml
                return false
            }
            File(dir).mkdirs()
            Files.copy(resource!!, configFile.toPath())
            PluginUtil.getInstance().consoleMessage(
                StartLang.getInstance().createMessage.replace("%to%", message).replace("%file%", configFile.name)
            )

            val finalConfigYaml = YamlFile(configFile)
            finalConfigYaml.loadWithComments()
            configList[source] = finalConfigYaml

            return true
        } catch (ex: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            return false
        }
    }

    private fun reloadHelper(dir: String) {
        for (to in configList.values) {
            val check: YamlFile
            try {
                val toLoad = YamlFile(File(dir, "${to.name}.yml"))
                toLoad.loadWithComments()
                check = toLoad
            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
                to.save(to.currentPath)
                return
            }
            check.save(to.currentPath)
        }
    }

    private fun configHelper(
        toCheckYaml: YamlFile,
        checkerYaml: YamlFile,
        configFile: File
    ) {
        var check = false
        for (fileKeys in checkerYaml.getKeys(true)) {
            if (toCheckYaml.get(fileKeys) == null) {
                check = true
                break
            }
        }
        for (fileKeys in toCheckYaml.getKeys(true)) {
            if (checkerYaml.get(fileKeys) == null && !check) {
                check = true
                break
            }
        }

        if (!check) return

        val toRemove = HashMap<String, Int>()

        val toPut = HashMap<String, Int>()

        val header = checkerYaml.options()
        val newHeader = toCheckYaml.options()
        if (header.header().toString() != newHeader.header().toString()) {
            checkerYaml.options().header(newHeader.header().toString())
            PluginUtil.getInstance()
                .consoleMessage(StartLang.getInstance().updateHeader.replace("%file%", configFile.name))
        }

        for (FileKeys in toCheckYaml.getValues(true)) {
            if (checkerYaml.get(FileKeys.key) == null) {
                toPut[FileKeys.key] = FileKeys.key.split(".").size.plus(1)
            }
        }

        for (createConfig in HashUtil.getInstance().hashMapSortMap(toPut)) {
            checkerYaml.set(createConfig.key, toCheckYaml.get(createConfig.key))
            if (toCheckYaml.getComment(createConfig.key) != null) {
                checkerYaml.setComment(createConfig.key, toCheckYaml.getComment(createConfig.key))
            }
            PluginUtil.getInstance().consoleMessage(
                StartLang.getInstance().addMessage.replace("%path%", createConfig.key)
                    .replace("%file%", configFile.name)
            )
        }

        for (FileKeys in checkerYaml.getValues(true)) {
            if (toCheckYaml.get(FileKeys.key) == null) {
                toRemove[FileKeys.key] = FileKeys.key.split(".").size.plus(1)
            }
        }

        for (deleteConfig in HashUtil.getInstance().hashMapReverse(HashUtil.getInstance().hashMapSortMap(toRemove))) {
            checkerYaml.set(deleteConfig.key, null)
            if (checkerYaml.getComment(deleteConfig.key) != null) {
                checkerYaml.setComment(deleteConfig.key, null)
            }
            PluginUtil.getInstance().consoleMessage(
                StartLang.getInstance().removeMessage.replace("%path%", deleteConfig.key)
                    .replace("%file%", configFile.name)
            )
        }

        checkerYaml.save(configFile)
    }

    companion object : IInstance<ConfigUtil> {
        private val instance = createInstance()
        override fun createInstance(): ConfigUtil = ConfigUtil()
        override fun getInstance(): ConfigUtil {
            return instance
        }
    }
}