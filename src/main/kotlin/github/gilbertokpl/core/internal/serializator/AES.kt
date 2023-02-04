package github.gilbertokpl.core.internal.serializator

import github.gilbertokpl.core.external.CorePlugin
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class AES(lf: CorePlugin) {

    private val lunarFrame = lf

    private val test = "250server"

    private lateinit var key: ByteArray
    fun encrypt(toEncrypt: String): String {
        val sha: MessageDigest?
        val secretKey: SecretKeySpec?
        try {
            key = test.toByteArray(charset("UTF-8"))
            sha = MessageDigest.getInstance("SHA-1")
            key = sha.digest(key)
            key = key.copyOf(16)
            secretKey = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return Base64.getEncoder().encodeToString(cipher.doFinal(toEncrypt.toByteArray(charset("UTF-8"))))
        } catch (e: Exception) {
            println("Error while encrypting: $e")
        }
        return "error"
    }

    fun decrypt(toDecrypt: String): String {
        val sha: MessageDigest?
        val secretKey: SecretKeySpec?
        try {
            key = test.toByteArray(charset("UTF-8"))
            sha = MessageDigest.getInstance("SHA-1")
            key = sha.digest(key)
            key = key.copyOf(16)
            secretKey = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            return String(cipher.doFinal(Base64.getDecoder().decode(toDecrypt)))
        } catch (e: Exception) {
            println("Error while decrypting: $e")
        }
        return "error"
    }
}