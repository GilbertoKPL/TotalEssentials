package github.gilbertokpl.core.external.utils

import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.core.internal.utils.InternalHost

class Host(lf: CorePlugin) {
    private val hostInstance = InternalHost(lf)

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

    internal fun start() {
        hostInstance.start()
    }

    fun getHost(): HostInfo {
        return hostInstance.getHost()
    }
}