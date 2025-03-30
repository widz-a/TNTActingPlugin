package wida.tntactingplugin.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import wida.tntactingplugin.TNTActingPluginMain

class PlayerJoinListener : TNTActingPluginMain.PluginListener() {
    @EventHandler
    fun join(event: PlayerJoinEvent) {
        event.player.sendMessage("HI")
    }
}