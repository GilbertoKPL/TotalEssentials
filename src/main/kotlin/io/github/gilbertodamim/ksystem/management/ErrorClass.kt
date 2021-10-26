package io.github.gilbertodamim.ksystem.management

class ErrorClass {
    fun sendException(error: Exception) {
        error.printStackTrace()
    }
}