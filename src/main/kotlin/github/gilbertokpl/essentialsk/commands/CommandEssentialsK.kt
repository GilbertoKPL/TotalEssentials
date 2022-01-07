package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.ConfigUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import oshi.SystemInfo
import oshi.hardware.CentralProcessor
import oshi.hardware.CentralProcessor.TickType
import java.io.File
import java.net.InetAddress
import java.nio.file.Files
import java.text.DecimalFormat


class CommandEssentialsK : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "essentialsk"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.essentialsk"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf("/essentialsk reload", "/essentialsk host")

    var oldTicks = LongArray(TickType.values().size)

    private var si = SystemInfo()

    private val format = DecimalFormat("0.00")

    private val percentagemValue = 100

    private val kbConversor = 1024 * 1024

    private val mhzConversor = 1000000

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

                val siProcessor = si.hardware.processor

                val siMemory = si.hardware.memory

                val ip = try {
                    InetAddress.getLocalHost().hostAddress
                } catch (e: Exception) {
                    "Unknow"
                }

                val os = System.getProperty("os.name") ?: "Unknown"

                val osVersion = System.getProperty("os.version") ?: "Unknown"

                val cpuName = if (siProcessor.processorIdentifier.name == "") {
                    "Unknown"
                } else {
                    siProcessor.processorIdentifier.name
                }

                val cpuMinMHZ = (try {
                    siProcessor.currentFreq[0] / mhzConversor
                } catch (e: Exception) {
                    "Unknown"
                }).toString()

                val cpuMaxMHZ = (siProcessor.maxFreq / mhzConversor).toString()

                val cpuUsage = format.format(floatArrayPercent(cpuData(siProcessor))[0])

                val cpuCores = "${siProcessor.physicalProcessorCount} / ${siProcessor.logicalProcessors.size}"

                val memMax = siMemory.total / kbConversor

                val memUsed = (siMemory.available / kbConversor - memMax) * -1

                val memServerMax = Runtime.getRuntime().maxMemory() / kbConversor

                val memServerUsed =
                    ((Runtime.getRuntime().freeMemory() - Runtime.getRuntime().totalMemory()) * -1) / kbConversor

                val hdName = try {
                    si.hardware.diskStores[0].name
                } catch (e: Exception) {
                    "Unknow"
                }

                val gpu = try {
                    si.hardware.graphicsCards[0].name
                } catch (e: Exception) {
                    "Unknow"
                }

                val file = Files.getFileStore(File("/").toPath())

                val totalHD = file.totalSpace / kbConversor

                val usedHD = totalHD - (file.usableSpace / kbConversor)

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
                                "%cpu_usage%", cpuUsage.toString()
                            )
                            .replace("%used_mem%", memUsed.toString())
                            .replace("%used_server_mem%", memServerUsed.toString())
                            .replace("%max_mem%", memMax.toString())
                            .replace("%max_server_mem%", memServerMax.toString())
                            .replace("%gpu%", gpu)
                            .replace("%name_hd%", hdName)
                            .replace("%used_hd%", usedHD.toString())
                            .replace("%max_hd%", totalHD.toString())
                    )
                }
            }
            return false
        }
        return true
    }

    private fun floatArrayPercent(d: Double): FloatArray {
        val f = FloatArray(1)
        f[0] = (percentagemValue * d).toFloat()
        return f
    }

    private fun cpuData(proc: CentralProcessor): Double {
        val d = proc.getSystemCpuLoadBetweenTicks(oldTicks)
        oldTicks = proc.systemCpuLoadTicks
        return d
    }
}
