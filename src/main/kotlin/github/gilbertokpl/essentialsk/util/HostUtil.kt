package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.configs.GeneralLang
import org.bukkit.command.CommandSender
import oshi.SystemInfo
import oshi.hardware.CentralProcessor
import java.io.File
import java.net.InetAddress
import java.nio.file.Files
import java.text.DecimalFormat

object HostUtil {
    private var oldTicks = LongArray(CentralProcessor.TickType.values().size)

    private var si = SystemInfo()

    private val format = DecimalFormat("0.00")

    private const val PERCENTAGE_VALUE = 100

    private const val KB_CONVERSOR = 1024 * 1024

    private const val MHZ_CONVERSOR = 1_000_000

    fun sendHostInfo(p: CommandSender) {
        TaskUtil.asyncExecutor {

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
                siProcessor.currentFreq[0] / MHZ_CONVERSOR
            } catch (e: Exception) {
                "Unknown"
            }).toString()

            val cpuMaxMHZ = (siProcessor.maxFreq / MHZ_CONVERSOR).toString()

            val cpuUsage = format.format(floatArrayPercent(cpuData(siProcessor))[0])

            val cpuCores = "${siProcessor.physicalProcessorCount} / ${siProcessor.logicalProcessors.size}"

            val memMax = siMemory.total / KB_CONVERSOR

            val memUsed = (siMemory.available / KB_CONVERSOR - memMax) * -1

            val memServerMax = Runtime.getRuntime().maxMemory() / KB_CONVERSOR

            val memServerUsed =
                ((Runtime.getRuntime().freeMemory() - Runtime.getRuntime().totalMemory()) * -1) / KB_CONVERSOR

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

            val totalHD = file.totalSpace / KB_CONVERSOR

            val usedHD = totalHD - (file.usableSpace / KB_CONVERSOR)

            val cpuServerCores = Runtime.getRuntime().availableProcessors()

            GeneralLang.generalHostConfigInfo.forEach {
                p.sendMessage(
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
    }

    private fun floatArrayPercent(d: Double): FloatArray {
        val f = FloatArray(1)
        f[0] = (PERCENTAGE_VALUE * d).toFloat()
        return f
    }

    private fun cpuData(proc: CentralProcessor): Double {
        val d = proc.getSystemCpuLoadBetweenTicks(oldTicks)
        oldTicks = proc.systemCpuLoadTicks
        return d
    }
}
