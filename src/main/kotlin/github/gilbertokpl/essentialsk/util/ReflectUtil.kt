package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.manager.EType
import github.gilbertokpl.essentialsk.manager.IInstance
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.simpleyaml.configuration.file.YamlFile
import java.io.File
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.javaType


class ReflectUtil {

    @OptIn(ExperimentalStdlibApi::class)
    fun setValuesOfClass(cl: KClass<*>, clInstance: Any, configList: List<YamlFile>) {
        for (it in cl.declaredMemberProperties) {
            if (it !is KMutableProperty<*>) continue
            val checkedValue = checkTypeField(it.returnType.javaType) ?: continue

            val nameFieldComplete = nameFieldHelper(it.name)

            for (yml in configList) {
                val value = checkedValue.getValueConfig(yml, nameFieldComplete) ?: continue
                it.setter.call(clInstance, value)
                break
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun setValuesFromClass(cl: KClass<*>, clInstance: Any, configList: List<YamlFile>, file: File) {
        for (it in cl.declaredMemberProperties) {
            if (it !is KMutableProperty<*>) continue
            val checkedValue = checkTypeField(it.returnType.javaType) ?: continue

            val nameFieldComplete = nameFieldHelper(it.name)

            for (yml in configList) {
                val value = checkedValue.setValueConfig(yml, nameFieldComplete) ?: continue
                value.set(nameFieldComplete, it.getter.call(clInstance))
                value.save(file)
                break
            }
        }
    }

    fun getPlayers(): List<Player> {
        @Suppress("UNCHECKED_CAST")
        return try {
            val onlinePlayersMethod  =
                Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers", *arrayOfNulls(0))
            if (onlinePlayersMethod.returnType.equals(MutableCollection::class.java)) (onlinePlayersMethod.invoke(
                Bukkit.getServer(),
                arrayOfNulls<Any>(0)
            ) as List<Player>) else (onlinePlayersMethod.invoke(
                Bukkit.getServer(),
                arrayOfNulls<Any>(0)
            ) as List<Player>)
        } catch (e: java.lang.Exception) {
            Bukkit.getOnlinePlayers().toList()
        }
    }

    private fun nameFieldHelper(name: String): String {
        val nameField = name.split("(?=\\p{Upper})".toRegex())

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


    private fun checkTypeField(type: Type): EType? {
        return when (type.typeName!!.lowercase()) {
            "java.lang.string" -> EType.STRING
            "java.util.list<java.lang.string>" -> EType.STRING_LIST
            "java.lang.boolean" -> EType.BOOLEAN
            "boolean" -> EType.BOOLEAN
            "java.lang.integer" -> EType.INTEGER
            "integer" -> EType.INTEGER
            else -> null
        }
    }

    companion object : IInstance<ReflectUtil> {
        private val instance = createInstance()
        override fun createInstance(): ReflectUtil = ReflectUtil()
        override fun getInstance(): ReflectUtil {
            return instance
        }
    }
}