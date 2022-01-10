package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.EssentialsK
import org.apache.commons.lang3.exception.ExceptionUtils
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.jar.Manifest

object ManifestUtil {
    private val attributes: HashMap<String, String> = HashMap()

    fun getManifestValue(key: String): String? {
        return attributes[key]
    }

    fun start() {
        try {
            val resources =
                EssentialsK.instance.javaClass.classLoader.getResources("META-INF/MANIFEST.MF")
            while (resources.hasMoreElements()) {
                val inputStream: InputStream = (resources.nextElement() as URL).openStream()
                val manifest = Manifest(inputStream)
                manifest.mainAttributes.forEach { key, value ->
                    attributes[key.toString()] = value as String
                }
                inputStream.close()
            }
        } catch (ioe: IOException) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ioe))
        }
    }
}
