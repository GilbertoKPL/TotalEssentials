package github.gilbertokpl.core.utils

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FileLogger {

    private val logDir = File("plugins/TotalEssentials/log")
    private val logFile = File(logDir, "log.txt")

    /**
     * Nível de log:
     * 1 = Apenas logs essenciais (erros, início/fim)
     * 2 = Logs detalhados (cada atualização individual)
     */
    var logLevel: Int = 1

    init {
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        if (!logFile.exists()) {
            logFile.createNewFile()
        }
    }

    private fun getCurrentDateTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        return current.format(formatter)
    }

    fun log(message: String) {
        try {
            val writer = FileWriter(logFile, true)
            writer.appendLine("${getCurrentDateTime()} - $message")
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Log com nível - só grava se logLevel >= level
     */
    fun log(message: String, level: Int) {
        if (logLevel >= level) {
            log(message)
        }
    }
}