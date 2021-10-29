package io.github.gilbertodamim.kcore.management

class ErrorClass {
    fun sendException(error: Exception) {
        error.printStackTrace()
    }
}