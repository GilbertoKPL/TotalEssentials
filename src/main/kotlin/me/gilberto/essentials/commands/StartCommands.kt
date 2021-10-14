package me.gilberto.essentials.commands

import me.gilberto.essentials.EssentialsMain.instance

class StartCommands {
    fun start() {
        instance.getCommand("essentials")?.setExecutor(Essentials())
        kits()
    }

    private fun kits() {
        if (me.gilberto.essentials.config.configs.Kits.activated) {
            Kits.startkits()
            instance.getCommand("kits")?.setExecutor(Kits())
        }
    }
}