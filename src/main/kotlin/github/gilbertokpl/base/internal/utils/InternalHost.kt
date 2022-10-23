package github.gilbertokpl.base.internal.utils

import github.gilbertokpl.base.external.BasePlugin
import github.gilbertokpl.base.external.utils.Host
import oshi.SystemInfo
import oshi.hardware.CentralProcessor
import java.io.File
import java.net.InetAddress
import java.nio.file.Files
import java.text.DecimalFormat

internal class InternalHost(lf: BasePlugin) {

    private val lunarFrame = lf

    private lateinit var si: SystemInfo

    private var oldTicks = LongArray(CentralProcessor.TickType.values().size)

    private val format = DecimalFormat("0.00")

    private val percentagemValue = 100

    private val gigaByteConversor = 1_073_741_824

    private val megaHertzConversor = 1_000_000

    private val ip = try {
        InetAddress.getLocalHost().hostAddress
    } catch (e: Throwable) {
        "Unknow"
    }

    private var cpuName = "Unknown"

    private var cpuCores = "Unknown"

    private val cpuServerCores = Runtime.getRuntime().availableProcessors()

    private var gpu = "Unknown"

    private val file = Files.getFileStore(File("/").toPath())

    private var diskName = "Unknown"

    private val diskMax = file.totalSpace / gigaByteConversor

    private var memMax = 0L

    private val memServerMax = Runtime.getRuntime().maxMemory() / gigaByteConversor

    private val osName = System.getProperty("os.name") ?: "Unknown"

    private val osVersion = System.getProperty("os.version") ?: "Unknown"

    fun start() {
        lunarFrame.getTask().async {

            si = SystemInfo()

            val siProcessor = si.hardware.processor

            val name = siProcessor.processorIdentifier.name

            val siMemory = si.hardware.memory

            cpuName = if (name == "") {
                when (siProcessor.processorIdentifier.model) {
                    "0xd0c" -> "Neoverse-N1"
                    "0xd49" -> "Neoverse-N2"
                    else -> "Unknown"
                }
            } else {
                name
            }

            cpuCores = "${siProcessor.physicalProcessorCount} / ${siProcessor.logicalProcessors.size}"

            gpu = try {
                si.hardware.graphicsCards[0].name
            } catch (e: Throwable) {
                "Unknown"
            }

            memMax = siMemory.total / gigaByteConversor

            diskName = try {
                si.hardware.diskStores[0].name
            } catch (e: Throwable) {
                "Unknow"
            }

        }
    }

    fun getHost(): Host.HostInfo {

        val siProcessor = si.hardware.processor

        val siMemory = si.hardware.memory

        val usedHD = diskMax - (file.usableSpace / gigaByteConversor)

        val memUsed = (siMemory.available / gigaByteConversor - memMax) * -1

        val memServerUsed =
            ((Runtime.getRuntime().freeMemory() - Runtime.getRuntime().totalMemory()) * -1) / gigaByteConversor

        val cpuUsage = format.format(floatArrayPercent(cpuData(siProcessor))[0])

        val cpuMinMHZ = (try {
            siProcessor.currentFreq[0] / megaHertzConversor
        } catch (e: Throwable) {
            "Unknown"
        }).toString()

        val cpuMaxMHZ = (siProcessor.maxFreq / megaHertzConversor).toString()

        return Host.HostInfo(
            ipAddress = ip,
            osName = osName,
            osVersion = osVersion,
            cpuName = cpuName,
            cpuCores = cpuCores,
            cpuUsage = cpuUsage,
            cpuAvailable = cpuServerCores.toString(),
            cpuClockMin = cpuMinMHZ,
            cpuClockMax = cpuMaxMHZ,
            memoryMax = memMax.toString(),
            memoryAllUsage = memUsed.toString(),
            memoryServerMax = memServerMax.toString(),
            memoryServerUsage = memServerUsed.toString(),
            gpuName = gpu,
            diskName = diskName,
            diskMax = diskMax.toString(),
            diskUsage = usedHD.toString()
        )

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