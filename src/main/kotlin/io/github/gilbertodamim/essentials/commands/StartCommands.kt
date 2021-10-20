package io.github.gilbertodamim.essentials.commands

import io.github.gilbertodamim.essentials.EssentialsMain.instance
import io.github.gilbertodamim.essentials.commands.kits.CreateKit
import io.github.gilbertodamim.essentials.commands.kits.DelKit
import io.github.gilbertodamim.essentials.commands.kits.EditKit
import io.github.gilbertodamim.essentials.commands.kits.Kit
import io.github.gilbertodamim.essentials.config.configs.KitsConfig

class StartCommands {
    fun start() {
        instance.getCommand("essentials")?.setExecutor(Essentials())
        kits()
    }

    private fun kits() {
        if (KitsConfig.activated) {
            Kit.startKits()
            instance.getCommand("kit")?.setExecutor(Kit())
            instance.getCommand("editkit")?.setExecutor(EditKit())
            instance.getCommand("createkit")?.setExecutor(CreateKit())
            instance.getCommand("delkit")?.setExecutor(DelKit())
        }
    }
}