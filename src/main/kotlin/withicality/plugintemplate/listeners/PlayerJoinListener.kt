package withicality.plugintemplate.listeners

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import withicality.plugintemplate.PluginTemplatePlugin
import withicality.plugintemplate.configs.ExampleConfig

class PlayerJoinListener : PluginTemplatePlugin.PluginListener() {
    @EventHandler
    fun join(event: PlayerJoinEvent) {
        val config = plugin.getConfig(ExampleConfig::class.java)
        event.player.sendMessage(MiniMessage.miniMessage().deserialize(config.getJoinMessage()))
    }
}