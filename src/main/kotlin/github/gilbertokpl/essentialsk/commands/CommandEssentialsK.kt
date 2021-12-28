package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.ConfigUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import oshi.SystemInfo
import java.io.File
import java.net.InetAddress
import java.nio.file.Files


class CommandEssentialsK : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "essentialsk"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.essentialsk"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf("/essentialsk reload", "/essentialsk host")

    private var si = SystemInfo()

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args[0] == "reload") {
            if (ConfigUtil.getInstance().reloadConfig(true)) {
                s.sendMessage(
                    GeneralLang.getInstance().generalConfigReload
                )
            }
            return false
        }
        if (args[0] == "host") {
            s.sendMessage(GeneralLang.getInstance().generalHostWait)
            TaskUtil.getInstance().asyncExecutor {

                val ip = InetAddress.getLocalHost().hostAddress

                val os = System.getProperty("os.name") ?: "Unknow"

                val osVersion = System.getProperty("os.version") ?: "Unknow"

                val cpuName = si.hardware.processor.processorIdentifier.name

                val cpuMinMHZ = (try {si.hardware.processor.currentFreq[0] / 1000000 } catch (e : Exception) { "Unknow" }).toString()

                val cpuMaxMHZ = (si.hardware.processor.maxFreq / 1000000).toString()

                val cpuUsage = "${si.hardware.processor.systemCpuLoadTicks.size}"

                val cpuTemp = "${si.hardware.sensors.cpuTemperature} C"

                val cpuFan = "${si.hardware.sensors.fanSpeeds.size} perc"

                val cpuVoltage = "${si.hardware.sensors.cpuVoltage} V"

                val cpuCores = "${si.hardware.processor.physicalProcessorCount} / ${si.hardware.processor.logicalProcessors.size}"

                val memMax = si.hardware.memory.total / (1024 * 1024)

                val memUsed = si.hardware.memory.available / (1024 * 1024)

                val memServerMax = Runtime.getRuntime().maxMemory() / (1024 * 1024)

                val memServerUsed = ((Runtime.getRuntime().freeMemory() - Runtime.getRuntime().totalMemory()) * -1) / (1024 * 1024)

                val hdName = try { si.hardware.diskStores[0].name } catch (e: Exception) { "Unknow" }

                val gpu = try { si.hardware.graphicsCards[0].name } catch (e: Exception) { "Unknow" }

                val file = Files.getFileStore(File("/").toPath())

                val totalHD = file.totalSpace / (1024 * 1024)

                val usedHD = totalHD - (file.usableSpace / (1024 * 1024))

                val cpuServerCores = Runtime.getRuntime().availableProcessors()

                GeneralLang.getInstance().generalHostConfigInfo.forEach {
                    s.sendMessage(
                        it.replace("%ip%", ip.toString())
                            .replace("%os%", os)
                            .replace("%os_version%", osVersion)
                            .replace("%cpu_name%", cpuName)
                            .replace("%cpu_clock_min%", cpuMinMHZ)
                            .replace("%cpu_clock_max%", cpuMaxMHZ)
                            .replace("%cores%", cpuCores)
                            .replace("%cores_server%", cpuServerCores.toString())
                            .replace(
                                "%cpu_usage%", if (cpuUsage.toInt() > 0) {
                                    cpuUsage.toInt().toString() + " perc"
                                } else {
                                    "Unknow"
                                }
                            )
                            .replace("%used_mem%", memUsed.toString())
                            .replace("%used_server_mem%", memServerUsed.toString())
                            .replace("%max_mem%", memMax.toString())
                            .replace("%max_server_mem%", memServerMax.toString())
                            .replace("%gpu%", gpu)
                            .replace("%name_hd%", hdName)
                            .replace("%cpu_temp%", cpuTemp)
                            .replace("%cpu_fan%", cpuFan)
                            .replace("%cpu_voltage%", cpuVoltage)
                            .replace("%used_hd%", usedHD.toString())
                            .replace("%max_hd%", totalHD.toString())
                    )
                }
            }
            return false
        }
        return true
    }
}