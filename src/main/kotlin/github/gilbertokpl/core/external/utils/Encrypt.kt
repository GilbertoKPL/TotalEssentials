package github.gilbertokpl.core.external.utils

import github.gilbertokpl.core.internal.serializator.AES

class Encrypt {
    private val encryptInstance = AES()

    fun encrypt(toEncrypt: String): String {
        return encryptInstance.encrypt(toEncrypt)
    }

    fun decrypt(toDecrypt: String): String {
        return encryptInstance.decrypt(toDecrypt)
    }

}