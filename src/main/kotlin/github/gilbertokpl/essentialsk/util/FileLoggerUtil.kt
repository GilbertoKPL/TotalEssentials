package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.manager.IInstance
import java.io.File
import java.time.LocalDateTime

import java.time.format.DateTimeFormatter


class FileLoggerUtil {

    private fun printError(fileName: String) {
        PluginUtil.getInstance()
            .consoleMessage("${EColor.RED.color}Error in plugin, saved in $fileName, please sent to plugin owner${EColor.RESET.color}")
    }

    fun logError(exception: String) {
        val dtf = DateTimeFormatter.ofPattern("HH-mm-ss_dd-MM-yyyy")
        val fileName = "/log/${dtf.format(LocalDateTime.now())}.txt"
        val file = File(PluginUtil.getInstance().mainPath, fileName)
        File(PluginUtil.getInstance().mainPath, "/log").mkdirs()

        printError(fileName)

        file.appendText(
            text = exception
        )
    }

    companion object : IInstance<FileLoggerUtil> {
        private val instance = createInstance()
        override fun createInstance(): FileLoggerUtil = FileLoggerUtil()
        override fun getInstance(): FileLoggerUtil {
            return instance
        }
    }
}