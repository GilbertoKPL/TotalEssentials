package io.github.gilbertodamim.kcore.commands

import io.github.gilbertodamim.kcore.KCoreMain.instance
import io.github.gilbertodamim.kcore.commands.kits.*
import io.github.gilbertodamim.kcore.config.configs.KitsConfig

class StartCommands {
    fun start() {
        instance.getCommand("kcore")?.setExecutor(KCore())
        kits()
    }

    private fun kits() {
        if (KitsConfig.activated) {
            Kit().startKits()
            instance.getCommand("kit")?.setExecutor(Kit())
            instance.getCommand("editkit")?.setExecutor(EditKit())
            instance.getCommand("createkit")?.setExecutor(CreateKit())
            instance.getCommand("delkit")?.setExecutor(DelKit())
            instance.getCommand("givekit")?.setExecutor(GiveKit())
        }
    }
}