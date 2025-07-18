package wida.tntactingplugin.listeners

import net.luckperms.api.LuckPermsProvider
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import wida.tntactingplugin.TNTActingPluginMain

class TabListener : TNTActingPluginMain.PluginListener() {
    @EventHandler
    fun join(event: PlayerJoinEvent) {
        val user = LuckPermsProvider.get().userManager.getUser(event.player.uniqueId) ?: return
        TNTActingPluginMain.Managers.tabManager.updatePrefix(user)
    }
}