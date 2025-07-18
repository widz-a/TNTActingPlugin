package wida.tntactingplugin.utils

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

object ComponentUtils {
    fun mojangToMiniMessage(string: String): String {
        val component = LegacyComponentSerializer.legacy('&').deserialize(string)
        return MiniMessage.miniMessage().serialize(component)
    }

    fun cmiToMiniMessage(cmiText: String): String {
        val withMiniHex = cmiText.replace("\\{#([A-Fa-f0-9]{6})}".toRegex()) {
            "<#${it.groupValues[1]}>"
        }

        val component = LegacyComponentSerializer.legacyAmpersand().deserialize(withMiniHex)
        return MiniMessage.miniMessage().serialize(component)
    }
}