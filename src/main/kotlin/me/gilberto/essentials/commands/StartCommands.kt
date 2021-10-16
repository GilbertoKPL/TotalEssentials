package me.gilberto.essentials.commands

import me.gilberto.essentials.EssentialsMain.instance
import me.gilberto.essentials.commands.kits.CreateKit
import me.gilberto.essentials.commands.kits.DelKit
import me.gilberto.essentials.commands.kits.EditKit
import me.gilberto.essentials.commands.kits.Kit

class StartCommands {
    fun start() {
        instance.getCommand("essentials")?.setExecutor(Essentials())
        kits()
    }

    private fun kits() {
        if (me.gilberto.essentials.config.configs.Kits.activated) {
            Kit.startkits()
            instance.getCommand("kit")?.setExecutor(Kit())
            instance.getCommand("editkit")?.setExecutor(EditKit())
            instance.getCommand("createkit")?.setExecutor(CreateKit())
            instance.getCommand("delkit")?.setExecutor(DelKit())
        }
    }
}