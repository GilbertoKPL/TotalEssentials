package github.gilbertokpl.core.utils

import github.gilbertokpl.core.TotalCore
import oshi.SystemInfo
import oshi.hardware.CentralProcessor
import java.io.File
import java.net.InetAddress
import java.nio.file.Files
import java.text.DecimalFormat

class HostUtil(lf: TotalCore) {
    private lateinit var si: SystemInfo

    private val corePlugin = lf

    private var oldTicks = LongArray(CentralProcessor.TickType.entries.size)

    private val format = DecimalFormat("0.00")

    private val percentageValue = 100

    private val gigaByteConverse = 1_073_741_824

    private val megaHertzConverse = 1_000_000

    private val ip = try {
        InetAddress.getLocalHost().hostAddress
    } catch (e: Throwable) {
        "Unknown"
    }

    private var cpuName = "Unknown"

    private var cpuCores = "Unknown"

    private val cpuServerCores = Runtime.getRuntime().availableProcessors()

    private var gpu = "Unknown"

    private val file = Files.getFileStore(File("/").toPath())

    private var diskName = "Unknown"

    private val diskMax = file.totalSpace / gigaByteConverse

    private var memMax = 0L

    private val memServerMax = Runtime.getRuntime().maxMemory() / gigaByteConverse

    private val osName = System.getProperty("os.name") ?: "Unknown"

    private val osVersion = System.getProperty("os.version") ?: "Unknown"

    data class HostInfo(
        val ipAddress: String,
        val osName: String,
        val osVersion: String,
        val cpuName: String,
        val cpuUsage: String,
        val cpuCores: String,
        val cpuAvailable: String,
        val cpuClockMin: String,
        val cpuClockMax: String,
        val memoryMax: String,
        val memoryAllUsage: String,
        val memoryServerMax: String,
        val memoryServerUsage: String,
        val gpuName: String,
        val diskName: String,
        val diskMax: String,
        val diskUsage: String,
    )

    fun start() {
        corePlugin.getTask().async {
            si = SystemInfo()

            val siProcessor = si.hardware.processor
            val name = siProcessor.processorIdentifier.name

            cpuName = when {
                name == "" -> when (siProcessor.processorIdentifier.model) {
                    "0xd0c" -> "Neoverse-N1"
                    "0xd49" -> "Neoverse-N2"
                    else -> "Unknown"
                }

                else -> name
            }

            cpuCores = "${siProcessor.physicalProcessorCount} / ${siProcessor.logicalProcessorCount}"

            gpu = try {
                si.hardware.graphicsCards[0].name
            } catch (e: Throwable) {
                "Unknown"
            }

            memMax = si.hardware.memory.total / gigaByteConverse

            diskName = try {
                si.hardware.diskStores[0].name
            } catch (e: Throwable) {
                "Unknown"
            }
        }
    }

    fun getHost(): HostInfo {
        val siProcessor = si.hardware.processor
        val siMemory = si.hardware.memory

        val usedHD = diskMax - (file.usableSpace / gigaByteConverse)
        val memUsed = (siMemory.available / gigaByteConverse - memMax) * -1
        val memServerUsed =
            ((Runtime.getRuntime().freeMemory() - Runtime.getRuntime().totalMemory()) * -1) / gigaByteConverse

        val cpuUsage = format.format(floatArrayPercent(cpuData(siProcessor))[0])
        val cpuMinMHZ = (try {
            siProcessor.currentFreq[0] / megaHertzConverse
        } catch (e: Throwable) {
            "Unknown"
        }).toString()
        val cpuMaxMHZ = (siProcessor.maxFreq / megaHertzConverse).toString()

        return HostInfo(
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
        f[0] = (percentageValue * d).toFloat()
        return f
    }

    private fun cpuData(proc: CentralProcessor): Double {
        val d = proc.getSystemCpuLoadBetweenTicks(oldTicks)
        oldTicks = proc.systemCpuLoadTicks
        return d
    }
}