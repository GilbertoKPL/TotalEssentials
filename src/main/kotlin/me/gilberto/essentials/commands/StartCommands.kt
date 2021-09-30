package me.gilberto.essentials.commands

import me.gilberto.essentials.EssentialsMain.instance

class StartCommands {
    fun start() {
        kits()
    }
    private fun kits() {
        Kits.startkits()
        instance.getCommand("kits").executor = Kits()
    }
}