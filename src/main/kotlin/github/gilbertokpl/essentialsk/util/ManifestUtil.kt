package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.manager.IInstance
import org.apache.commons.lang3.exception.ExceptionUtils
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.jar.Manifest


class ManifestUtil {
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
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ioe))
        }
    }

    companion object : IInstance<ManifestUtil> {
        private val instance = createInstance()
        override fun createInstance(): ManifestUtil = ManifestUtil()
        override fun getInstance(): ManifestUtil {
            return instance
        }
    }
}