package github.gilbertokpl.essentialsk.commands

import com.profesorfalken.jsensors.JSensors
import com.sun.management.OperatingSystemMXBean
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.manager.ICommand
import github.gilbertokpl.essentialsk.util.ConfigUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.io.File
import java.lang.management.ManagementFactory
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

    private val osBean: OperatingSystemMXBean? =
        ManagementFactory.getPlatformMXBean(OperatingSystemMXBean::class.java) ?: null
    private val ip = InetAddress.getLocalHost().hostAddress ?: "Unknow"
    private val os = System.getProperty("os.name") ?: "Unknow"
    private val osVersion = System.getProperty("os.version") ?: "Unknow"
    private val cpuCores = Runtime.getRuntime().availableProcessors()

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
                val cpuUsage = (osBean?.processCpuLoad?.times(100)) ?: 0
                val memMax = Runtime.getRuntime().maxMemory() / (1024 * 1024)
                val memUsed =
                    ((Runtime.getRuntime().freeMemory() - Runtime.getRuntime().totalMemory()) * -1) / (1024 * 1024)
                val cpuInfo = ArrayList<String>()
                val hdInfo = ArrayList<String>()
                val manager = JSensors.get
                val components = manager.components()

                val compCpu = components.cpus

                var cpu = 0

                for (i in compCpu) {
                    for (to in 1 until i.sensors.loads.size - 1) {
                        val temp = if (i.sensors.temperatures[to - 1].value.toInt() != 0) {
                            ", ${i.sensors.temperatures[to - 1].value.toInt()} °C"
                        } else {
                            ""
                        }
                        cpuInfo.add("CPU = $to, ${i.sensors.loads[to - 1].value.toInt()} %$temp")
                    }
                    cpu += 1
                }

                val cpuName = try { "${cpu}X ${compCpu[0].name}" } catch (e: Exception) { "Unknow" }

                for (i in components.disks) {
                    hdInfo.add("${i.name}, ${i.sensors.loads.size} %, ${i.sensors.temperatures.size} °C")
                }

                val gpu = try { components.gpus[0].name } catch (e: Exception) { "Unknow" }

                val file = Files.getFileStore(File("/").toPath())

                val totalHD = file.totalSpace / (1024 * 1024)

                val usedHD = totalHD - (file.usableSpace / (1024 * 1024))

                GeneralLang.getInstance().generalHostConfig.forEach {
                    s.sendMessage(
                        it.replace("%ip%", ip)
                            .replace("%os%", os)
                            .replace("%os_version%", osVersion)
                            .replace("%cpu_name%", cpuName)
                            .replace("%cpu_info%", cpuInfo.toString())
                            .replace("%cores%", cpuCores.toString())
                            .replace(
                                "%cpu_usage%", if (cpuUsage.toInt() > 0) {
                                    cpuUsage.toInt().toString()
                                } else {
                                    "Unknow"
                                }
                            )
                            .replace("%used_mem%", memUsed.toString())
                            .replace("%max_mem%", memMax.toString())
                            .replace("%hd_info%", hdInfo.toString())
                            .replace("%gpu%", gpu)
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