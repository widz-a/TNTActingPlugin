package wida.tntactingplugin.utils

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

object ComponentUtils {
    fun mojangToMiniMessage(string: String): String {
        val component = LegacyComponentSerializer.legacyAmpersand().deserialize(string)
        return MiniMessage.miniMessage().serialize(component)
    }

    fun miniMessageToMojang(string: String): String {
        val component = MiniMessage.miniMessage().deserialize(string)
        return LegacyComponentSerializer.legacyAmpersand().serialize(component)
    }
}