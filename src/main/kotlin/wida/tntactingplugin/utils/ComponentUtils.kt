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

        val withMiniLegacy = withMiniHex.replace("&([0-9a-fk-or])".toRegex(RegexOption.IGNORE_CASE)) {
            when (it.groupValues[1].lowercase()) {
                "0" -> "<black>"
                "1" -> "<dark_blue>"
                "2" -> "<dark_green>"
                "3" -> "<dark_aqua>"
                "4" -> "<dark_red>"
                "5" -> "<dark_purple>"
                "6" -> "<gold>"
                "7" -> "<gray>"
                "8" -> "<dark_gray>"
                "9" -> "<blue>"
                "a" -> "<green>"
                "b" -> "<aqua>"
                "c" -> "<red>"
                "d" -> "<light_purple>"
                "e" -> "<yellow>"
                "f" -> "<white>"
                "k" -> "<obfuscated>"
                "l" -> "<bold>"
                "m" -> "<strikethrough>"
                "n" -> "<underlined>"
                "o" -> "<italic>"
                "r" -> "<reset>"
                else -> it.value
            }
        }

        return withMiniLegacy
    }
}