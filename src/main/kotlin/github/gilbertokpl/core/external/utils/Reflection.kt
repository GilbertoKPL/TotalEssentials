package github.gilbertokpl.core.external.utils

import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.core.external.config.types.ObjectTypes
import github.gilbertokpl.core.internal.utils.InternalReflection
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.simpleyaml.configuration.file.YamlFile
import java.lang.reflect.Field

class Reflection(lf: CorePlugin) {
    private val reflectionInstance = InternalReflection(lf)

    fun getClasses(packageName: String): List<Class<*>> {
        return reflectionInstance.getClasses(packageName)
    }

    fun bukkitCommandRegister(command: Command) {
        reflectionInstance.bukkitCommandRegister(command)
    }

    fun nameFieldHelper(field: Field): String {
        return reflectionInstance.nameFieldHelper(field)
    }

    fun setValuesOfClass(cl: Class<*>, clInstance: Any, config: YamlFile) {
        reflectionInstance.setValuesOfClass(cl, clInstance, config)
    }

    fun registerCommandByPackage(pa: String) {
        reflectionInstance.registerCommandByPackage(pa)
    }

    fun getPlayers(): List<Player> {
        return reflectionInstance.getPlayers()
    }

    fun getHealth(p: Player): Double {
        return reflectionInstance.getHealth(p)
    }

    fun setHealth(p: Player, health: Int) {
        reflectionInstance.setHealth(p, health)
    }

    private fun checkTypeField(type: java.lang.reflect.Type): ObjectTypes? {
        return checkTypeField(type)
    }
}