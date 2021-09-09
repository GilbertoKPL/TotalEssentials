package me.gilberto.essentials

object EssentialsInstance {
    fun startInstance(main: EssentialsMain) {
        instance = main
    }
    lateinit var instance: EssentialsMain
}