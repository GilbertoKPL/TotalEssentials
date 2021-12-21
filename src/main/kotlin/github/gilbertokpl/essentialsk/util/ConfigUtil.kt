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
    lateinit var configYaml : YamlFile

    lateinit var langYaml : YamlFile

    private var bol = false

    fun start() {
        PluginUtil.getInstance().consoleMessage(StartLang.getInstance().startVerification.replace("%to%", "config"))

        startFun("configs", false)

        PluginUtil.getInstance().consoleMessage(StartLang.getInstance().completeVerification)

        PluginUtil.getInstance().consoleMessage(StartLang.getInstance().startVerification.replace("%to%", "lang"))

        startFun("langs", true)

        OtherConfigUtil.getInstance().start()

        PluginUtil.getInstance().consoleMessage(StartLang.getInstance().completeVerification)
    }

    internal fun getString(source: YamlFile, path: String, color: Boolean = true): String {
        return if (color) {
            source.getString(path)!!.replace("&", "§")
        } else source.getString(path)!!
    }

    internal fun getStringList(source: YamlFile, path: String, color: Boolean = true): List<String> {
        if (source.filePath.contains("CommandsConfig.yml") || source.filePath.contains("ProtectConfig.yml")) {
            return source.getStringList(path).stream().map { to -> to.lowercase() }.collect(Collectors.toList())
        }
        return if (color) {
            source.getStringList(path).stream().map { to -> to.replace("&", "§") }.collect(Collectors.toList())
        } else source.getStringList(path)
    }

    internal fun getInt(source: YamlFile, path: String): Int = source.getInt(path)

    internal fun getBoolean(source: YamlFile, path: String): Boolean = source.getBoolean(path)

    private fun internalReloadConfig() {
        ReflectUtil.getInstance().setValuesFromClass(
            MainConfig::class.java,
            MainConfig.getInstance(),
            configYaml
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
                if (!lang) {
                    bol = initializeYml(i.fileName.toString().replace(".yml", ""), false)
                    continue
                }
                initializeYml(i.fileName.toString().replace(".yml", ""), true)
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

                val langY = YamlFile(langHelper())
                langY.load()

                this@ConfigUtil.langYaml = langY

                ReflectUtil.getInstance()
                    .setValuesOfClass(GeneralLang::class.java, GeneralLang.getInstance(), langYaml)

            } catch (ex: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            }
            return
        }

        try {
            ReflectUtil.getInstance()
                .setValuesOfClass(MainConfig::class.java, MainConfig.getInstance(), configYaml)
        } catch (ex: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
        }
    }

    private fun langHelper(): File {
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
        return langSelected
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

                this.configYaml[source] = finalConfigYaml
                return false
            }
            File(dir).mkdirs()
            Files.copy(resource!!, configFile.toPath())
            PluginUtil.getInstance().consoleMessage(
                StartLang.getInstance().createMessage.replace("%to%", message).replace("%file%", configFile.name)
            )
            if (!lang) {
                val finalConfigYaml = YamlFile(configFile)
                finalConfigYaml.loadWithComments()
                configYaml[source] = finalConfigYaml
            }

            return true
        } catch (ex: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
            return false
        }
    }

    fun reloadConfig(all: Boolean) {
        if (all) {
            startFun("configs", false)

            startFun("langs", true)

            OtherConfigUtil.getInstance().start()

            return
        }
    }

    private fun configHelper(
        toCheckYaml: YamlFile,
        checkerYaml: YamlFile,
        configFile: File
    ) {
        var check = false
        for (fileKeys in checkerYaml.getValues(true)) {
            if (toCheckYaml.get(fileKeys.key) == null) {
                check = true
                break
            }
        }
        for (fileKeys in toCheckYaml.getValues(true)) {
            if (checkerYaml.get(fileKeys.key) == null && !check) {
                check = true
                break
            }
        }
        for (fileKeys in checkerYaml.getValues(true)) {
            val comment = toCheckYaml.getComment(fileKeys.key)
            if (checkerYaml.getComment(fileKeys.key) != comment) {
                check = true
                break
            }
        }

        if (!check) return

        val toRemove = HashMap<String, Int>()

        val toPut = HashMap<String, Int>()

        //values
        for (FileKeys in toCheckYaml.getValues(true)) {
            if (checkerYaml.get(FileKeys.key) == null) {
                toPut[FileKeys.key] = FileKeys.key.split(".").size.plus(1)
                continue
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

        //values
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

        //comments
        for (FileKeys in checkerYaml.getValues(true)) {
            val comment = toCheckYaml.getComment(FileKeys.key)
            if (comment == null) {
                checkerYaml.setComment(FileKeys.key, null)
                continue
            }
            //comments
            if (checkerYaml.getComment(FileKeys.key) != comment) {
                checkerYaml.setComment(FileKeys.key, comment)
            }
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