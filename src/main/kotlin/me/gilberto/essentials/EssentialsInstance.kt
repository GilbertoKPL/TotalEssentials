package me.gilberto.essentials

object EssentialsInstance {
    fun StartInstance(main: EssentialsMain) {
        instance = main
    }
    lateinit var instance: EssentialsMain
}