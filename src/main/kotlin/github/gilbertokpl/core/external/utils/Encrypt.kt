package github.gilbertokpl.core.external.utils

import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.core.internal.serializator.AES

class Encrypt(lf: CorePlugin) {
    private val encryptInstance = AES(lf)

    fun encrypt(toEncrypt: String): String {
        return encryptInstance.encrypt(toEncrypt)
    }

    fun decrypt(toDecrypt: String): String {
        return encryptInstance.decrypt(toDecrypt)
    }

}