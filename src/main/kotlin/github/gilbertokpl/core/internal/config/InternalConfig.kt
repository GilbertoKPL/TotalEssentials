package github.gilbertokpl.core.internal.config

import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.core.external.config.annotations.Comments
import github.gilbertokpl.core.external.config.annotations.ConfigPattern
import github.gilbertokpl.core.external.config.annotations.PrimaryComments
import github.gilbertokpl.core.external.config.annotations.Values
import github.gilbertokpl.core.external.config.def.DefaultConfig
import github.gilbertokpl.core.external.config.def.DefaultLang
import github.gilbertokpl.core.external.config.types.LangTypes
import org.simpleyaml.configuration.file.YamlFile
import java.io.File

internal class InternalConfig(private val corePlugin: CorePlugin) {

    lateinit var messages: DefaultLang
    lateinit var configs: DefaultConfig

    private var lang = LangTypes.PT_BR

    data class NewConfig(
        val javaClass: Class<*>,
        val classInstance: Any,
        val file: File,
        val lang: LangTypes?
    )

    fun start(configPackage: String) {
        val listClass = corePlugin.getReflection().getClasses(configPackage).toMutableList()

        var mainConfigClass: Class<*>? = null

        for (cl in listClass) {
            lateinit var values: ConfigPattern

            for (clAnnotations in cl.annotations) {
                if (clAnnotations is ConfigPattern) {
                    values = clAnnotations
                    break
                }
            }

            val instance = cl.getDeclaredConstructor().newInstance()

            if (instance is DefaultConfig) {
                configs = instance
                startClass(
                    NewConfig(
                        cl,
                        instance,
                        File(corePlugin.mainPath, "${values.name}.yml"),
                        null
                    ),
                    true
                )
                mainConfigClass = cl
                break
            }
        }

        listClass.remove(mainConfigClass)

        for (cl in listClass) {
            val instance = cl.getDeclaredConstructor().newInstance()

            if (instance is DefaultLang) {
                messages = instance

                for (i in LangTypes.values()) {
                    startClass(
                        NewConfig(
                            cl,
                            instance,
                            i.getFile(corePlugin),
                            i
                        ),
                        false
                    )
                }
                continue
            }

            var values: ConfigPattern? = null

            for (clAnnotations in cl.annotations) {
                if (clAnnotations is ConfigPattern) {
                    values = clAnnotations
                    break
                }
            }

            startClass(
                NewConfig(
                    cl,
                    instance,
                    File(corePlugin.mainPath, "${values?.name}.yml"),
                    null
                ),
                null
            )
        }
    }

    private fun startClass(config: NewConfig, mainConfig: Boolean?) {
        val configFile = config.file
        if (!configFile.exists()) {
            val yamlFile = genYaml(config, config.lang ?: LangTypes.PT_BR)
            yamlFile.save(configFile)
            if (config.lang == null || config.lang == lang) {
                load(config, yamlFile)
            }
            return
        }

        val currentConfig = loadYaml(configFile, false)

        if (mainConfig == true) {
            lang = try {
                LangTypes.valueOf(currentConfig.getString("general.selected-lang").uppercase())
            } catch (il: IllegalArgumentException) {
                LangTypes.PT_BR
            }
        }
        if (mainConfig == false && lang == config.lang) {
            corePlugin.serverPrefix = try {
                currentConfig.getString("general.server-prefix").replace("&", "§")
            } catch (_: IllegalArgumentException) {
                ""
            } catch (_: NullPointerException) {
                ""
            }
        }

        val checkYaml = genYaml(config, lang)

        val toSet = HashMap<String, Any?>()
        val toRemove = ArrayList<String>()
        val toAdd = HashMap<String, Any?>()

        for (i in currentConfig.getValues(true).toMap()) {
            if (checkYaml.get(i.key) == null) {
                toRemove.add(i.key)
            } else {
                toSet[i.key] = i.value
            }
        }

        for (i in checkYaml.getValues(true)) {
            if (currentConfig.get(i.key) == null) {
                if (i.key.contains(".")) {
                    toAdd[i.key] = i.value
                }
            }
        }

        for (i in toSet) {
            if (toRemove.contains(i.key)) continue
            checkYaml.set(i.key, i.value)
        }

        for (i in toRemove) {
            checkYaml.set(i, null)
        }

        for (i in toAdd) {
            checkYaml.set(i.key, i.value)
        }

        checkYaml.save(configFile)

        if (config.lang == null || config.lang == lang) {
            load(config, checkYaml)
        }
    }

    private fun load(newConfig: NewConfig, yamlFile: YamlFile) {
        corePlugin.getReflection().setValuesOfClass(newConfig.javaClass, newConfig.classInstance, yamlFile)
    }

    private fun loadYaml(file: File, comment: Boolean): YamlFile {
        val yamlFile = YamlFile(file)
        if (comment) {
            yamlFile.loadWithComments()
        } else {
            yamlFile.load()
        }
        return yamlFile
    }

    private fun genYaml(newConfig: NewConfig, lang: LangTypes): YamlFile {
        val tempYamlFile = YamlFile()

        for (i in newConfig.javaClass.declaredFields) {
            val path = corePlugin.getReflection().nameFieldHelper(i)

            if (newConfig.lang == null) {
                tempYamlFile.set(path, i.get(newConfig.classInstance))
            }

            val commentsList = mutableListOf<Comments>()
            var primaryComments: PrimaryComments? = null
            var values: Values? = null

            for (comments in i.annotations) {
                when (comments) {
                    is Comments -> commentsList.add(comments)
                    is PrimaryComments -> primaryComments = comments
                    is Values -> values = comments
                }
            }

            for (comments in commentsList) {
                for (com in comments.value) {
                    if (com.annotations != "" && lang == com.lang) {
                        tempYamlFile.setComment(path, com.annotations)
                    }
                }
            }

            primaryComments?.let {
                for (com in it.value) {
                    if (com.primaryAnnotations != "" && lang == com.lang) {
                        tempYamlFile.setComment(path.split(".")[0], com.primaryAnnotations)
                    }
                }
            }

            values?.let {
                for (com in it.value) {
                    if (lang == com.lang) {
                        if (com.value.contains("|")) {
                            tempYamlFile.set(path, com.value.split("|").toList())
                        } else {
                            tempYamlFile.set(path, com.value)
                        }
                    }
                }
            }
        }

        return tempYamlFile
    }
}