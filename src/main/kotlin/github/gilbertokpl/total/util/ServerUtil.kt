package github.gilbertokpl.total.util

import github.gilbertokpl.core.utils.ConsoleColorUtil
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.inventory.EditKit
import github.gilbertokpl.total.cache.inventory.Kit
import github.gilbertokpl.total.cache.inventory.Playtime
import github.gilbertokpl.total.cache.inventory.Shop
import github.gilbertokpl.total.cache.loop.AntiAfkLoop
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.economy.MoneyManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ServerUtil {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val specialCharactersRegex = Regex("[^A-Za-z0-9 ]")

    /**
     * Retorna a hora local atual formatada (HH:mm)
     */
    fun getCurrentTime(): String {
        return LocalDateTime.now().format(timeFormatter)
    }

    /**
     * Retorna um elemento aleatório de uma lista
     */
    fun getRandom(list: List<String>): String {
        return list.random()
    }

    /**
     * Envia mensagem colorida no console com prefixo do plugin
     */
    fun consoleMessage(message: String) {
        val pluginName = TotalEssentials.getInstance().name
        println("${ConsoleColorUtil.CYAN.color}[$pluginName]${ConsoleColorUtil.RESET.color} $message")
    }

    /**
     * Envia mensagem broadcast para todos os jogadores online
     * Usa reflection para compatibilidade entre versões antigas e novas
     */
    fun broadcastMessage(message: String) {
        TotalEssentials.getCore().getReflection().getPlayers().forEach { player ->
            player.sendMessage(message)
        }
    }

    /**
     * Inicializa todos os sistemas e features do plugin baseado na configuração
     */
    fun initializeFeatures() {
        if (MainConfig.kitsActivated) {
            EditKit.setup()
            Kit.setup()
        }

        if (MainConfig.shopActivated) {
            Shop.setup()
        }

        if (MainConfig.antiafkEnabled) {
            AntiAfkLoop.start()
        }

        if (MainConfig.playtimeActivated) {
            Playtime.setup()
        }

        if (MainConfig.moneyActivated) {
            MoneyManager.refreshTycoon()
        }
    }

    /**
     * Verifica se a string contém caracteres especiais (não alfanuméricos)
     * @return true se contém caracteres especiais, false caso contrário
     */
    fun hasSpecialCharacters(text: String?): Boolean {
        return text?.contains(specialCharactersRegex) ?: false
    }

    /**
     * Verifica se a string contém APENAS caracteres alfanuméricos e espaços
     * @return true se é válida, false se contém caracteres especiais
     */
    fun isAlphanumeric(text: String?): Boolean {
        return !hasSpecialCharacters(text)
    }
}