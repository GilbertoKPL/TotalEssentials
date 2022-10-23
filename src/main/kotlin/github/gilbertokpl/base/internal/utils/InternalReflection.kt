package github.gilbertokpl.base.internal.utils

import github.gilbertokpl.base.external.BasePlugin
import github.gilbertokpl.base.external.command.CommandCreator
import github.gilbertokpl.base.external.config.types.ObjectTypes
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.entity.Player
import org.simpleyaml.configuration.file.YamlFile
import java.lang.reflect.Field
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths

internal class InternalReflection(lf: BasePlugin) {

    private val lunarFrame = lf

    private var getPlayersList: Boolean? = null
    fun getClasses(packageName: String): List<Class<*>> {
        val classes = ArrayList<Class<*>>()
        val directory = try {
            val path = packageName.replace('.', '/')
            Files.newDirectoryStream(
                FileSystems.newFileSystem(
                    Paths.get(
                        this.lunarFrame.plugin.javaClass.protectionDomain?.codeSource?.location?.toURI()
                            ?: return emptyList()
                    ),
                    this.lunarFrame.plugin.javaClass.classLoader
                ).getPath("/$path")
            )
        } catch (x: NullPointerException) {
            return emptyList()
        }
        for (i in directory) {
            if (i.fileName.toString().endsWith(".class")) {
                if (i.fileName.toString().contains("$")) continue
                val cla = Class.forName(packageName + "." + i.fileName.toString().replace(".class", ""))
                classes.add(cla)
            }
        }
        return classes.toList()
    }

    fun bukkitCommandRegister(command: Command) {
        val bukkitCommandMap: Field = Bukkit.getServer().javaClass.getDeclaredField("commandMap")

        bukkitCommandMap.isAccessible = true
        val commandMap: CommandMap = bukkitCommandMap.get(Bukkit.getServer()) as CommandMap

        commandMap.register("lunarFrame", command)
    }

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

            val value = checkedValue.getValueConfig(config, nameFieldComplete, lunarFrame) ?: continue
            it.set(clInstance, value)
        }
    }

    private fun checkTypeField(type: java.lang.reflect.Type): ObjectTypes? {
        return when (type.typeName!!.lowercase()) {
            "java.lang.string" -> ObjectTypes.STRING
            "java.util.list<java.lang.string>" -> ObjectTypes.STRING_LIST
            "java.lang.boolean" -> ObjectTypes.BOOLEAN
            "boolean" -> ObjectTypes.BOOLEAN
            "java.lang.integer" -> ObjectTypes.INTEGER
            "integer" -> ObjectTypes.INTEGER
            "int" -> ObjectTypes.INTEGER
            else -> {
                null
            }
        }
    }

    fun registerCommandByPackage(pa: String) {
        val listClass = getClasses(pa)

        for (cl in listClass) {

            val instance = cl.getDeclaredConstructor().newInstance() as CommandCreator

            instance.basePlugin = lunarFrame
            instance.aliases = instance.commandPattern().aliases
            instance.active = instance.commandPattern().active
            instance.target = instance.commandPattern().target
            instance.permission = instance.commandPattern().permission
            instance.commandUsage = instance.commandPattern().usage
            instance.countdown = instance.commandPattern().countdown
            instance.minimumSize = instance.commandPattern().minimumSize
            instance.maximumSize = instance.commandPattern().maximumSize

            if (!instance.commandPattern().active) continue

            bukkitCommandRegister(instance)
        }

    }

    fun getPlayers(): List<Player> {
        if (getPlayersList == null) {
            val onlinePlayersMethod = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers")
            return try {
                @Suppress("UNCHECKED_CAST")
                val to = (onlinePlayersMethod.invoke(Bukkit.getServer()) as Array<Player>).toList()
                getPlayersList = true
                to
            } catch (e: ClassCastException) {
                getPlayersList = false
                Bukkit.getOnlinePlayers().toList()
            }
        }
        return if (getPlayersList!!) {
            val list = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers")
            @Suppress("UNCHECKED_CAST")
            (list.invoke(Bukkit.getServer()) as Array<Player>).toList()
        } else {
            Bukkit.getOnlinePlayers().toList()
        }
    }

    fun getHealth(p: Player): Double {
        return try {
            p.health
        } catch (e: Throwable) {
            try {
                (p.javaClass.getMethod("getHealth").invoke(p, arrayListOf<Int>()) as Int).toDouble()
            } catch (e1: Throwable) {
                0.0
            }
        }
    }

    fun setHealth(p: Player, health: Int) {
        try {
            p.health = health.toDouble()
        } catch (e: Throwable) {
            try {
                p.javaClass.getMethod("setHealth").invoke(
                    p, health
                )
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}