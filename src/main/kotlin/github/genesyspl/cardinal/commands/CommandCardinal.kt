package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.configs.GeneralLang
import github.genesyspl.cardinal.manager.ICommand
import github.genesyspl.cardinal.util.ConfigUtil
import github.genesyspl.cardinal.util.TaskUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import oshi.SystemInfo
import oshi.hardware.CentralProcessor.TickType
import java.io.File
import java.net.InetAddress
import java.nio.file.Files


class CommandCardinal : ICommand {
    override val consoleCanUse: Boolean = true
    override val commandName = "cardinal"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.cardinal"
    override val minimumSize = 1
    override val maximumSize = 1
    override val commandUsage = listOf("/cardinal reload", "/cardinal host")

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

                val siProcessor = si.hardware.processor

                val siMemory = si.hardware.memory

                val ip = try {
                    InetAddress.getLocalHost().hostAddress
                } catch (e: Exception) {
                    "Unknow"
                }

                val os = System.getProperty("os.name") ?: "Unknow"

                val osVersion = System.getProperty("os.version") ?: "Unknow"

                val cpuName = if (siProcessor.processorIdentifier.name == "") {
                    "Unknow"
                } else {
                    siProcessor.processorIdentifier.name
                }

                val cpuMinMHZ = (try {
                    siProcessor.currentFreq[0] / 1000000
                } catch (e: Exception) {
                    "Unknow"
                }).toString()

                val cpuMaxMHZ = (siProcessor.maxFreq / 1000000).toString()

                val prevTicks = LongArray(TickType.values().size)

                val cpuUsage = (siProcessor.getSystemCpuLoadBetweenTicks(prevTicks) * 100).toInt()

                val cpuCores = "${siProcessor.physicalProcessorCount} / ${siProcessor.logicalProcessors.size}"

                val memMax = siMemory.total / (1024 * 1024)

                val memUsed = (siMemory.available / (1024 * 1024) - memMax) * -1

                val memServerMax = Runtime.getRuntime().maxMemory() / (1024 * 1024)

                val memServerUsed =
                    ((Runtime.getRuntime().freeMemory() - Runtime.getRuntime().totalMemory()) * -1) / (1024 * 1024)

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
}