package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.configs.OtherConfig
import github.gilbertokpl.essentialsk.configs.StartLang
import github.gilbertokpl.essentialsk.inventory.EditKitInventory
import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import github.gilbertokpl.essentialsk.manager.ELang
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.simpleyaml.configuration.file.YamlFile
import java.io.File
import java.nio.file.*
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors

internal object ConfigUtil {
    lateinit var configYaml: YamlFile

    lateinit var langYaml: YamlFile

    private val lowercaseValues = listOf(
        "nicks.blocked-nicks",
        "homes.block-worlds",
        "vanish.blocked-other-cmds",
        "back.disabled-worlds",
        "fly.disabled-worlds",
        "containers.block-shift",
        "containers.block-open",
        "discordbot.command-chat"
    )

    private var bol = false

    fun start() {
        MainUtil.consoleMessage(StartLang.startVerification.replace("%to%", "config"))

        startFun("configs", false)

        MainUtil.consoleMessage(StartLang.completeVerification)

        MainUtil.consoleMessage(StartLang.startVerification.replace("%to%", "lang"))

        startFun("langs", true)

        OtherConfigUtil.start()

        MainUtil.consoleMessage(StartLang.completeVerification)
    }

    fun getString(source: YamlFile, path: String, color: Boolean = true): String? {
        return if (color) {
            PermissionUtil.colorPermission(null, source.getString(path))
                .replace("%prefix%", OtherConfig.serverPrefix)
        } else source.getString(path)
    }

    fun getStringList(source: YamlFile, path: String, color: Boolean = true): List<String> {
        if (lowercaseValues.contains(path)) {
            return source.getStringList(path).stream().map { to -> to.lowercase() }.collect(Collectors.toList())
        }
        return if (color) {
            source.getStringList(path).stream()
                .map { to -> PermissionUtil.colorPermission(null, to).replace("%prefix%", OtherConfig.serverPrefix) }
                .collect(Collectors.toList())
        } else source.getStringList(path)
    }

    fun getInt(source: YamlFile, path: String): Int = source.getInt(path)

    fun getBoolean(source: YamlFile, path: String): Boolean = source.getBoolean(path)

    private fun internalReloadConfig() {
        ReflectUtil.setValuesFromClass(
            MainConfig::class.java,
            MainConfig(),
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
                    } catch (e: Throwable) {
                        "OTHER"
                    }
                    val enum = try {
                        ELang.valueOf(timezone).locale
                    } catch (e: Throwable) {
                        ELang.valueOf("OTHER").locale
                    }
                    MainConfig.generalSelectedLang = enum
                    internalReloadConfig()
                }

                val langY = YamlFile(langHelper())
                langY.load()

                langYaml = langY

                OtherConfig.serverPrefix = getString(langYaml, "general.server-prefix") ?: ""

                ReflectUtil
                    .setValuesOfClass(GeneralLang::class.java, GeneralLang(), langYaml)

            } catch (ex: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
            }
            return
        }

        try {
            ReflectUtil
                .setValuesOfClass(MainConfig::class.java, MainConfig(), configYaml)
        } catch (ex: Throwable) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
        }
    }

    private fun langHelper(): File {
        var langSelected =
            File(MainUtil.langPath, "${MainConfig.generalSelectedLang}.yml")

        if (langSelected.exists()) {
            MainUtil.consoleMessage(
                StartLang.langSelectedMessage.replace(
                    "%lang%",
                    "${MainConfig.generalSelectedLang}.yml"
                )
            )
        } else {
            langSelected = File(MainUtil.langPath, "pt_BR.yml")
            MainUtil.consoleMessage(StartLang.langError)
        }
        return langSelected
    }

    private fun initializeYml(source: String, lang: Boolean = false): Boolean {
        val location: String
        val dir: String
        val message: String
        if (lang) {
            location = "/langs/$source.yml"
            dir = MainUtil.langPath
            message = "lang"
        } else {
            location = "/configs/$source.yml"
            dir = MainUtil.mainPath
            message = "config"
        }
        try {
            val configFile = File(dir, "$source.yml")
            val resource = EssentialsK.instance.javaClass.getResourceAsStream(location)
            if (configFile.exists()) {
                val internalConfigYaml = YamlFile(configFile)
                internalConfigYaml.loadWithComments()
                if (resource == null) return false
                val tempFile = File.createTempFile("check", ".yml")
                FileUtils.copyToFile(resource, tempFile)

                val tempConfig = YamlFile(tempFile)
                tempConfig.loadWithComments()

                configHelper(
                    tempConfig,
                    internalConfigYaml,
                    configFile
                )

                tempFile.delete()

                if (!lang) {
                    val finalConfigYaml = YamlFile(configFile)
                    finalConfigYaml.loadWithComments()
                    configYaml = finalConfigYaml
                }
                return false
            }
            File(dir).mkdirs()
            Files.copy(resource!!, configFile.toPath())
            MainUtil.consoleMessage(
                StartLang.createMessage.replace("%to%", message).replace("%file%", configFile.name)
            )
            if (!lang) {
                val finalConfigYaml = YamlFile(configFile)
                finalConfigYaml.loadWithComments()
                configYaml = finalConfigYaml
            }

            return true
        } catch (ex: Throwable) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
            return false
        }
    }

    fun reloadConfig(all: Boolean): Boolean {
        if (all) {
            startFun("configs", false)

            startFun("langs", true)

            OtherConfigUtil.start()

            EditKitInventory.setup()

            KitGuiInventory.setup()

            if (MainConfig.discordbotConnectDiscordChat && DiscordUtil.jda == null) {
                CompletableFuture.runAsync {
                    DiscordUtil.startBot()
                }
            }

            return true
        }
        return false
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


        for (createConfig in HashUtil.hashMapSortMap(toPut)) {
            checkerYaml.set(createConfig.key, toCheckYaml.get(createConfig.key))
            if (toCheckYaml.getComment(createConfig.key) != null) {
                checkerYaml.setComment(createConfig.key, toCheckYaml.getComment(createConfig.key))
            }
            MainUtil.consoleMessage(
                StartLang.addMessage.replace("%path%", createConfig.key)
                    .replace("%file%", configFile.name)
            )
        }

        //values
        for (FileKeys in checkerYaml.getValues(true)) {
            if (toCheckYaml.get(FileKeys.key) == null) {
                toRemove[FileKeys.key] = FileKeys.key.split(".").size.plus(1)
            }
        }

        for (deleteConfig in HashUtil.hashMapReverse(HashUtil.hashMapSortMap(toRemove))) {
            checkerYaml.set(deleteConfig.key, null)
            if (checkerYaml.getComment(deleteConfig.key) != null) {
                checkerYaml.setComment(deleteConfig.key, null)
            }
            MainUtil.consoleMessage(
                StartLang.removeMessage.replace("%path%", deleteConfig.key)
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
}
