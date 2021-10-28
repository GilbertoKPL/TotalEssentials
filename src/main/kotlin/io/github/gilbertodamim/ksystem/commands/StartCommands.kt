package io.github.gilbertodamim.ksystem.commands

import io.github.gilbertodamim.ksystem.KSystemMain.instance
import io.github.gilbertodamim.ksystem.commands.kits.CreateKit
import io.github.gilbertodamim.ksystem.commands.kits.DelKit
import io.github.gilbertodamim.ksystem.commands.kits.EditKit
import io.github.gilbertodamim.ksystem.commands.kits.Kit
import io.github.gilbertodamim.ksystem.config.configs.KitsConfig

class StartCommands {
    fun start() {
        instance.getCommand("ksystem")?.setExecutor(KSystem())
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