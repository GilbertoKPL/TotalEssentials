package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.manager.EType
import github.gilbertokpl.essentialsk.manager.IInstance
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.SimpleCommandMap
import org.bukkit.entity.Player
import org.simpleyaml.configuration.file.YamlFile
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Type
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit


class ReflectUtil {

    private var getPlayersList: Boolean? = null

    fun removeCommand(cmd: String) {
        CompletableFuture.runAsync({
            TimeUnit.SECONDS.sleep(20)
            try {
                val commandMapField: Field = Bukkit.getPluginManager().javaClass.getDeclaredField("commandMap")
                val commandsField: Field = SimpleCommandMap::class.java.getDeclaredField("knownCommands")
                commandMapField.isAccessible = true
                commandsField.isAccessible = true
                val commandMap: CommandMap = commandMapField.get(Bukkit.getPluginManager()) as CommandMap
                val commands = commandsField[commandMap] as HashMap<String, Command>
                val toRemove = HashMap<String, Command>()
                for (entry in commands.entries) {
                    if (entry.value.name.lowercase() == cmd || entry.key.split(":").size > 1 && entry.key.lowercase()
                            .split(":")[1] == cmd
                    ) {
                        toRemove[entry.key] = entry.value
                    }
                }
                toRemove.forEach {
                    commands.remove(it.key, it.value)
                }
                commandMap.getCommand(cmd)?.unregister(commandMap)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }, TaskUtil.getInstance().getExecutor())
    }

    fun setValuesOfClass(cl: Class<*>, clInstance: Any, config: YamlFile) {
        for (it in cl.declaredFields) {
            val checkedValue = checkTypeField(it.genericType) ?: continue

            val nameFieldComplete = nameFieldHelper(it.name)

            val value = checkedValue.getValueConfig(config, nameFieldComplete) ?: continue
            it.set(clInstance, value)
        }
    }

    fun setValuesFromClass(cl: Class<*>, clInstance: Any, config: YamlFile) {
        for (it in cl.declaredFields) {
            val checkedValue = checkTypeField(it.genericType) ?: continue

            val nameFieldComplete = nameFieldHelper(it.name)

            val value = checkedValue.setValueConfig(config, nameFieldComplete) ?: continue
            value.set(nameFieldComplete, it.get(clInstance))
            value.save(File(config.filePath))
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
            "int" -> EType.INTEGER
            else -> {
                null
            }
        }
    }

    fun getHealth(p: Player): Double {
        return try {
            p.health
        } catch (e: NoSuchMethodError) {
            try {
                (p.javaClass.getMethod("getHealth", *arrayOfNulls(0)).invoke(p, *arrayOfNulls(0)) as Int).toInt()
                    .toDouble()
            } catch (e1: Throwable) {
                0.0
            }
        }
    }

    fun setHealth(p: Player, health: Int) {
        try {
            p.javaClass.getMethod("setHealth", Double::class.javaPrimitiveType)
                .invoke(p, java.lang.Double.valueOf(health.toDouble()))
        } catch (e: Throwable) {
            try {
                p.javaClass.getMethod("setHealth", Int::class.javaPrimitiveType).invoke(
                    p, Integer.valueOf(health)
                )
            } catch (e: Throwable) {
                e.printStackTrace()
            }
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