package github.gilbertokpl.total.util

import org.bukkit.entity.Player

/**
 * Entrada segura para mensagens clicáveis. As classes do chat do Bungee ficam
 * isoladas em [FancyChatComponents], permitindo que servidores legados
 * continuem carregando o plugin sem essas classes.
 */
object FancyChat {

    data class Action(
        val label: String,
        val command: String,
        val hover: String = "§7$command"
    )

    private val supported by lazy {
        try {
            val spigotClass = Player::class.java.getMethod("spigot").returnType
            val baseComponent = Class.forName("net.md_5.bungee.api.chat.BaseComponent")
            Class.forName("net.md_5.bungee.api.chat.TextComponent")
            Class.forName("net.md_5.bungee.api.chat.ClickEvent")
            Class.forName("net.md_5.bungee.api.chat.HoverEvent")
            spigotClass.methods.any { method ->
                method.name == "sendMessage" &&
                    method.parameterTypes.size == 1 &&
                    method.parameterTypes[0].isArray &&
                    method.parameterTypes[0].componentType == baseComponent
            }
        } catch (_: Throwable) {
            false
        }
    }

    /**
     * Envia o texto e, na linha seguinte, ações que executam comandos.
     * Retorna false quando a API não está disponível para permitir fallback.
     */
    fun sendActions(
        player: Player,
        message: String,
        actions: List<Action>,
        separator: String = " §8| "
    ): Boolean {
        if (!supported || actions.isEmpty()) return false

        return try {
            FancyChatComponents.send(player, "$message\n", actions, separator, "")
            true
        } catch (_: Throwable) {
            false
        }
    }

    /**
     * Substitui um placeholder de uma mensagem por uma lista clicável.
     */
    fun sendCommandList(
        player: Player,
        template: String,
        placeholder: String,
        actions: List<Action>
    ): Boolean {
        if (!supported || actions.isEmpty() || !template.contains(placeholder)) return false

        val prefix = template.substringBefore(placeholder)
        val suffix = template.substringAfter(placeholder)

        return try {
            FancyChatComponents.send(player, prefix, actions, "§7, ", suffix)
            true
        } catch (_: Throwable) {
            false
        }
    }
}
