package wida.tntactingplugin.managers

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.mineacademy.fo.remain.Remain

class StaffManager {
    fun sendLog(sender: CommandSender, message: String, permissionSuffix: String? = null) {
        val component = Component.text("[${sender.name}: $message]")
            .color(NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, true)

        val permission = "tnt.log${if (permissionSuffix != null) ".$permissionSuffix" else ""}"

        Remain.getOnlinePlayers().forEach {
            if (!it.hasPermission(permission)) return@forEach
            it.sendMessage(component)
        }

        Bukkit.getConsoleSender().sendMessage(component)
    }

    fun sendLog(sender: CommandSender, component: Component, permissionSuffix: String? = null) {
        sendLog(sender, PlainTextComponentSerializer.plainText().serialize(component), permissionSuffix)
    }
}