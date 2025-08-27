package github.gilbertokpl.core.internal.utils

import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.core.external.command.CommandCreator
import github.gilbertokpl.core.external.config.types.ObjectTypes
import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.simpleyaml.configuration.file.YamlFile
import java.lang.reflect.Field
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths

internal class InternalReflection(private val corePlugin: CorePlugin) {

    private var getPlayersList: Boolean? = null

    fun getClasses(packageName: String): List<Class<*>> {
        val classes = mutableListOf<Class<*>>()
        val directory = try {
            val path = packageName.replace('.', '/')
            Files.newDirectoryStream(
                FileSystems.newFileSystem(
                    Paths.get(
                        this.corePlugin.plugin.javaClass.protectionDomain?.codeSource?.location?.toURI()
                            ?: return emptyList()
                    ),
                    this.corePlugin.plugin.javaClass.classLoader
                ).getPath("/$path")
            )
        } catch (x: NullPointerException) {
            return emptyList()
        }
        for (i in directory) {
            if (i.fileName.toString().endsWith(".class") && !i.fileName.toString().contains("$")) {
                val className = packageName + "." + i.fileName.toString().replace(".class", "")
                val cla = Class.forName(className)
                classes.add(cla)
            }
        }
        return classes
    }


    fun bukkitCommandRegister(cmd: CommandCreator) {
        val register = {
            try {
                val bukkitCommand = object : org.bukkit.command.Command(
                    cmd.name,
                    cmd.commandUsage.toString(),
                    cmd.commandUsage.toString(),
                    cmd.aliases.toList()
                ) {
                    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
                        return cmd.execute(sender, label, args)
                    }
                }

                // pega CommandMap via reflexão
                val commandMapField = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
                commandMapField.isAccessible = true
                val commandMap = commandMapField.get(Bukkit.getServer()) as CommandMap

                commandMap.register("TotalEssentials", bukkitCommand)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 🔥 Usa Folia se disponível, senão Bukkit normal
        val scheduler = foliaSchedulerExecute
        if (scheduler != null) {
            scheduler(corePlugin.plugin, Runnable { register() })
        } else {
            Bukkit.getScheduler().runTask(corePlugin.plugin, Runnable { register() })
        }
    }

    private val foliaSchedulerExecute: ((Plugin, Runnable) -> Unit)? by lazy {
        try {
            val server = Bukkit.getServer()
            val schedulerClass = server.javaClass.getMethod("getGlobalRegionScheduler")
            val scheduler = schedulerClass.invoke(server)
            val executeMethod = scheduler.javaClass.getMethod("execute", Plugin::class.java, Runnable::class.java)

            return@lazy { plugin: Plugin, task: Runnable ->
                executeMethod.invoke(scheduler, plugin, task)
            }
        } catch (_: Throwable) {
            return@lazy null
        }
    }


    fun nameFieldHelper(field: Field): String {
        val nameField = field.name.split("(?=\\p{Upper})".toRegex())
        val nameFieldComplete = StringBuilder()
        var quanta = 0
        for (value in nameField) {
            quanta += 1
            if (nameFieldComplete.isEmpty()) {
                nameFieldComplete.append("${value.lowercase()}.")
                continue
            }
            if (quanta == 2) {
                nameFieldComplete.append(value.lowercase())
                continue
            }
            nameFieldComplete.append("-${value.lowercase()}")
        }
        return nameFieldComplete.toString()
    }

    fun setValuesOfClass(cl: Class<*>, clInstance: Any, config: YamlFile) {
        for (field in cl.declaredFields) {
            val checkedValue = checkTypeField(field.genericType) ?: continue
            val nameFieldComplete = nameFieldHelper(field)
            val value = checkedValue.getValueConfig(config, nameFieldComplete, corePlugin) ?: continue
            field.set(clInstance, value)
        }
    }

    private fun checkTypeField(type: java.lang.reflect.Type): ObjectTypes? {
        return when (type.typeName!!.lowercase()) {
            "java.lang.string" -> ObjectTypes.STRING
            "java.util.list<java.lang.string>" -> ObjectTypes.STRING_LIST
            "java.lang.boolean", "boolean" -> ObjectTypes.BOOLEAN
            "java.lang.integer", "integer", "int" -> ObjectTypes.INTEGER
            else -> null
        }
    }

    fun registerCommandByPackage(packageName: String) {
        val listClass = getClasses(packageName)

        for (cl in listClass) {
            try {
                val instance = cl.getDeclaredConstructor().newInstance() as CommandCreator

                // aplica padrões do commandPattern
                instance.basePlugin = corePlugin
                instance.commandPattern().let { pattern ->
                    instance.aliases = pattern.aliases
                    instance.active = pattern.active
                    instance.target = pattern.target
                    instance.permission = pattern.permission
                    instance.commandUsage = pattern.usage
                    instance.countdown = pattern.countdown
                    instance.minimumSize = pattern.minimumSize
                    instance.maximumSize = pattern.maximumSize
                }

                if (!instance.active) continue

                // aqui entra o registro seguro compatível com Paper + Folia
                bukkitCommandRegister(instance)

            } catch (e: Exception) {
                Bukkit.getLogger().warning("Falha ao registrar comando da classe ${cl.name}: ${e.message}")
                e.printStackTrace()
            }
        }
    }


    fun getPlayers(): List<Player> {
        if (getPlayersList == null) {
            val onlinePlayersMethod = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers")
            return try {
                @Suppress("UNCHECKED_CAST")
                val players = onlinePlayersMethod.invoke(Bukkit.getServer()) as Array<Player>
                getPlayersList = true
                players.toList()
            } catch (e: ClassCastException) {
                getPlayersList = false
                Bukkit.getOnlinePlayers().toList()
            }
        }
        return if (getPlayersList == true) {
            val list = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers")
            @Suppress("UNCHECKED_CAST")
            (list.invoke(Bukkit.getServer()) as Array<Player>).toList()
        } else {
            Bukkit.getOnlinePlayers().toList()
        }
    }

    fun getHealth(player: Player): Double {
        return try {
            player.health
        } catch (e: Throwable) {
            try {
                (player.javaClass.getMethod("getHealth").invoke(player, arrayListOf<Int>()) as Int).toDouble()
            } catch (e1: Throwable) {
                0.0
            }
        }
    }

    fun setHealth(player: Player, health: Int) {
        try {
            player.health = health.toDouble()
        } catch (e: Throwable) {
            try {
                player.javaClass.getMethod("setHealth").invoke(player, health)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}
