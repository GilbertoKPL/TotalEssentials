package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.EssentialsK
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.SimpleCommandMap
import org.bukkit.entity.Player
import org.simpleyaml.configuration.file.YamlFile
import java.io.File
import java.lang.reflect.Field
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths

internal object ReflectUtil {

    private var getPlayersList: Boolean? = null

    fun removeCommand(cmd: String) {
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

    fun <T> getClasses(packageName: String): List<T> {
        val classes = ArrayList<T>()
        val directory = try {
            val path = packageName.replace('.', '/')
            Files.newDirectoryStream(
                FileSystems.newFileSystem(
                    Paths.get(EssentialsK.instance.javaClass.protectionDomain.codeSource.location.toURI()),
                    EssentialsK.instance.javaClass.classLoader
                ).getPath("/$path")
            )
        } catch (x: NullPointerException) {
            return emptyList()
        }
        for (i in directory) {
            if (i.fileName.toString().endsWith(".class")) {
                if (i.fileName.toString().contains("$")) continue
                val cla = Class.forName(packageName + i.fileName.toString().replace(".class", ""))
                classes.add(cla.newInstance() as T)
            }
        }

        return classes.toList()
    }

}
