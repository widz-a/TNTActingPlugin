package wida.tntactingplugin.listeners

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import wida.tntactingplugin.TNTActingPluginMain

class NoSpectatorListener : TNTActingPluginMain.PluginListener() {
    @EventHandler
    fun gamemodeSwitch(event: PlayerCommandPreprocessEvent) {
        val message = event.message
        if (event.player.hasPermission("tnt.iamallowedtodospectatorbecauseiamcoolaf")) return
        if (message.startsWith("/gamemode spectator") || message.startsWith("/gmsp")) {
            event.isCancelled = true
            event.player.sendMessage(Component.text("Denied.").color(NamedTextColor.RED))
        }
    }
}