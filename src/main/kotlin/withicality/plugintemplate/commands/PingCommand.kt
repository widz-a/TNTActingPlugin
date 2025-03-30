package withicality.plugintemplate.commands

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Syntax
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import withicality.plugintemplate.PluginTemplatePlugin

class PingCommand : PluginTemplatePlugin.PluginCommand() {

    @CommandAlias("ping")
    @Syntax("<player>")
    @Description("Get player's ping")
    fun other(sender: CommandSender, player: OnlinePlayer) {
        sender.sendMessage(player.player.displayName().color(NamedTextColor.LIGHT_PURPLE)
            .append(MiniMessage.miniMessage().deserialize("<reset>'s ping is <light_purple>${player.player.ping}ms<white>.")))
    }
}