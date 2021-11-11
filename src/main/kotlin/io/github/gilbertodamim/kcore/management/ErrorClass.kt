package io.github.gilbertodamim.kcore.management

internal object ErrorClass {
    fun sendException(error: Exception) {
        error.printStackTrace()
    }
}