package github.gilbertokpl.base.internal.config

import github.gilbertokpl.base.external.BasePlugin
import github.gilbertokpl.base.external.config.annotations.Comments
import github.gilbertokpl.base.external.config.annotations.ConfigPattern
import github.gilbertokpl.base.external.config.annotations.PrimaryComments
import github.gilbertokpl.base.external.config.annotations.Values
import github.gilbertokpl.base.external.config.def.DefaultConfig
import github.gilbertokpl.base.external.config.def.DefaultLang
import github.gilbertokpl.base.external.config.types.LangTypes
import org.simpleyaml.configuration.file.YamlFile
import java.io.File
import java.nio.charset.Charset

internal class InternalConfig(lf: BasePlugin) {

    private val lunarFrame = lf

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
        val listClass = lunarFrame.getReflection().getClasses(configPackage).toMutableList()

        lateinit var toRemove: Class<*>

        for (cl in listClass) {
            lateinit var values: ConfigPattern

            for (clAnnotations in cl.annotations) {
                if (clAnnotations is ConfigPattern) {
                    values = clAnnotations
                }
            }

            val instance = cl.getDeclaredConstructor().newInstance()

            if (instance is DefaultConfig) {

                configs = instance

                startClass(
                    NewConfig(
                        cl,
                        instance,
                        File(lunarFrame.mainPath, "${values.name}.yml"),
                        null
                    ),
                    true
                )
                toRemove = cl
                break
            }
        }

        listClass.remove(toRemove)

        for (cl in listClass) {

            val instance = cl.getDeclaredConstructor().newInstance()

            if (instance is DefaultLang) {

                messages = instance

                for (i in LangTypes.values()) {
                    startClass(
                        NewConfig(
                            cl,
                            instance,
                            i.getFile(lunarFrame),
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
                }
            }

            startClass(
                NewConfig(
                    cl,
                    instance,
                    File(lunarFrame.mainPath, "${values?.name}.yml"),
                    null
                ),
                null
            )
        }
    }

    private fun startClass(config: NewConfig, mainConfig: Boolean?) {

        if (!config.file.exists()) {

            val file = genYaml(config, config.lang ?: LangTypes.PT_BR)

            file.save(config.file)

            if (config.lang == null || config.lang == lang) {
                load(config, file)
                return
            }

            return
        }

        val currentConfig = loadYaml(config.file, false)

        if (mainConfig == true) {
            lang = try {
                LangTypes.valueOf(currentConfig.getString("general.selected-lang").uppercase())
            } catch (il: IllegalArgumentException) {
                LangTypes.PT_BR
            }
        }
        if (mainConfig == false && lang == config.lang) {
            lunarFrame.serverPrefix = try {
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

        checkYaml.save(config.file)

        if (config.lang == null || config.lang == lang) {
            load(config, checkYaml)

            return
        }

    }

    private fun load(newConfig: NewConfig, yamlFile: YamlFile) {
        lunarFrame.getReflection().setValuesOfClass(newConfig.javaClass, newConfig.classInstance, yamlFile)
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

        val tempFile = File.createTempFile("check", ".yml")

        val tempYamlFile = loadYaml(tempFile, true)

        tempFile.delete()

        for (i in newConfig.javaClass.declaredFields) {
            val path = lunarFrame.getReflection().nameFieldHelper(i)


            if (newConfig.lang == null) {
                tempYamlFile.set(path, i.get(newConfig.classInstance))
            }

            for (comments in i.annotations) {
                if (comments is Comments) {
                    for (com in comments.value) {
                        if (com.annotations != "" && lang == com.lang) {
                            tempYamlFile.setComment(path, com.annotations)
                        }
                    }
                    continue
                }
                if (comments is PrimaryComments) {
                    for (com in comments.value) {
                        if (com.primaryAnnotations != "" && lang == com.lang) {
                            tempYamlFile.setComment(path.split(".")[0], com.primaryAnnotations)
                        }
                    }
                    continue
                }
                if (comments is Values) {
                    for (com in comments.value) {
                        if (lang == com.lang) {
                            if (com.value.contains("|")) {
                                tempYamlFile.set(path, com.value.split("|").toList())
                                continue
                            }
                            tempYamlFile.set(path, com.value)
                        }
                    }
                    continue
                }
            }
        }

        return tempYamlFile
    }
}