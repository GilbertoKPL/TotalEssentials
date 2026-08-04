package github.gilbertokpl.total.util

import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player

/**
 * Implementação carregada somente quando o servidor oferece a API de
 * BaseComponent usada por Player.Spigot#sendMessage.
 */
internal object FancyChatComponents {

    fun send(
        player: Player,
        prefix: String,
        actions: List<FancyChat.Action>,
        separator: String,
        suffix: String
    ) {
        val components = ArrayList<BaseComponent>()
        components.addAll(TextComponent.fromLegacyText(prefix))

        actions.forEachIndexed { index, action ->
            if (index > 0) components.addAll(TextComponent.fromLegacyText(separator))

            val clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, action.command)
            val hoverEvent = HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                TextComponent.fromLegacyText(action.hover)
            )

            TextComponent.fromLegacyText(action.label).forEach { component ->
                component.isBold = true
                component.isUnderlined = true
                component.clickEvent = clickEvent
                component.hoverEvent = hoverEvent
                components.add(component)
            }
        }

        components.addAll(TextComponent.fromLegacyText(suffix))
        player.spigot().sendMessage(*components.toTypedArray())
    }
}
