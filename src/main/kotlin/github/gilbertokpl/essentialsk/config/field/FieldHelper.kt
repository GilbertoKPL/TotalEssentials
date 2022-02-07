package github.gilbertokpl.essentialsk.config.field

import github.gilbertokpl.essentialsk.config.types.Type
import org.simpleyaml.configuration.file.YamlFile
import java.io.File
import java.lang.reflect.Field

object FieldHelper {

    fun nameFieldHelper(field: Field): String {
        val nameField = field.name.split("(?=\\p{Upper})".toRegex())

        var nameFieldComplete = ""

        var quanta = 0

        for (value in nameField) {
            quanta += 1
            if (nameFieldComplete == "") {
                nameFieldComplete = "${value.lowercase()}."
                continue
            }
            if (quanta == 2) {
                nameFieldComplete += value.lowercase()
                continue
            }
            nameFieldComplete += "-${value.lowercase()}"
        }

        return nameFieldComplete
    }

    fun setValuesOfClass(cl: Class<*>, clInstance: Any, config: YamlFile) {
        for (it in cl.declaredFields) {
            val checkedValue = checkTypeField(it.genericType) ?: continue

            val nameFieldComplete = nameFieldHelper(it)

            val value = checkedValue.getValueConfig(config, nameFieldComplete) ?: continue
            it.set(clInstance, value)
        }
    }

    fun setValuesFromClass(cl: Class<*>, clInstance: Any, config: YamlFile) {
        for (it in cl.declaredFields) {
            val checkedValue = checkTypeField(it.genericType) ?: continue

            val nameFieldComplete = nameFieldHelper(it)

            val value = checkedValue.setValueConfig(config, nameFieldComplete) ?: continue
            value.set(nameFieldComplete, it.get(clInstance))
            value.save(File(config.filePath))
        }
    }

    private fun checkTypeField(type: java.lang.reflect.Type): Type? {
        return when (type.typeName!!.lowercase()) {
            "java.lang.string" -> Type.STRING
            "java.util.list<java.lang.string>" -> Type.STRING_LIST
            "java.lang.boolean" -> Type.BOOLEAN
            "boolean" -> Type.BOOLEAN
            "java.lang.integer" -> Type.INTEGER
            "integer" -> Type.INTEGER
            "int" -> Type.INTEGER
            else -> {
                null
            }
        }
    }
}