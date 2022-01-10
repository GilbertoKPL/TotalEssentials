package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.manager.EColor
import org.json.simple.JSONObject
import java.io.File
import java.io.IOException
import java.net.URL
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.CompletableFuture
import javax.net.ssl.HttpsURLConnection

object FileLoggerUtil {

    var logger: String? = null

    private fun printLogger(exception: String, time: String) {
        if (logger == null) return
        CompletableFuture.runAsync({
            try {
                var svName = MainConfig.generalServerName
                svName = if (svName == "") {
                    "Server - unamed, Time - $time"
                } else {
                    "Server - $svName, Time - $time"
                }
                val url = URL(logger)
                val payload = JSONObject()
                payload["username"] = svName
                payload["content"] = "```$exception```"
                val connection = url.openConnection() as HttpsURLConnection
                connection.addRequestProperty("Content-Type", "application/json")
                connection.addRequestProperty("User-Agent", "EssentialsK")
                connection.doOutput = true
                connection.requestMethod = "POST"
                val stream = connection.outputStream
                val str3: String = payload.toString()
                val charset: Charset = StandardCharsets.UTF_8
                val arrayOfByte = str3.toByteArray(charset)
                stream.write(arrayOfByte)
                stream.flush()
                stream.close()
                connection.inputStream.close()
                connection.disconnect()

            } catch (e: IOException) {
                logger = null
            }
        }, TaskUtil.getExecutor())
    }

    private fun printError(fileName: String) {
        MainUtil.consoleMessage(
            EColor.RED.color +
                    "Error in plugin" +
                    ", saved in $fileName" +
                    ", please sent to plugin owner" +
                    EColor.RESET.color
        )
    }

    fun logError(exception: String) {
        val dtf = DateTimeFormatter.ofPattern("HH-mm-ss_dd-MM-yyyy")
        val fileName = "/log/${dtf.format(LocalDateTime.now())}.txt"
        val file = File(MainUtil.mainPath, fileName)
        File(MainUtil.mainPath, "/log").mkdirs()

        printError(fileName)

        printLogger(exception, dtf.format(LocalDateTime.now()))

        file.appendText(
            text = exception
        )
    }
}
