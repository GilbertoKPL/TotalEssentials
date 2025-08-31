package github.gilbertokpl.core.utils

import github.gilbertokpl.core.serializator.AES

class EncryptUtil {
    private val encryptInstance = AES()

    fun encrypt(toEncrypt: String): String {
        return encryptInstance.encrypt(toEncrypt)
    }

    fun decrypt(toDecrypt: String): String {
        return encryptInstance.decrypt(toDecrypt)
    }

}