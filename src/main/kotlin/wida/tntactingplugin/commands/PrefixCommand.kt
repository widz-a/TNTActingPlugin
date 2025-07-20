package wida.tntactingplugin.commands

import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.HelpCommand
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import net.kyori.adventure.text.minimessage.MiniMessage
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.node.Node
import org.bukkit.command.CommandSender
import wida.tntactingplugin.TNTActingPluginMain
import wida.tntactingplugin.utils.ComponentUtils

@CommandAlias("prefix")
@CommandPermission("tnt.commands.prefix")
class PrefixCommand : TNTActingPluginMain.PluginCommand() {
    @Subcommand("set")
    @Syntax("<player> <prefix: MiniMessage>")
    @Description("Set a player's prefix")
    fun prefix(sender: CommandSender, player: OnlinePlayer, prefix: String) {
        val api = LuckPermsProvider.get()
        val user = api.userManager.loadUser(player.player.uniqueId).join()

        user.getNodes()
            .filter { it.key.startsWith("prefix.143.") }
            .forEach { user.data().remove(it) }

        user.data().add(Node.builder("prefix.143.${ComponentUtils.miniMessageToMojang(prefix)}").build())
        user.data().add(Node.builder("suffix.143.").build())
        api.userManager.saveUser(user)

        val message = MiniMessage.miniMessage().deserialize("<yellow>Set <gold>${player.player.name} <yellow>'s prefix to <reset>${prefix}<reset><yellow>.")
        sender.sendMessage(message)
        TNTActingPluginMain.Managers.staffManager.sendLog(sender, message, "prefix")
    }

    @Subcommand("reset")
    @Syntax("<player>")
    @Description("Reset a player's prefix")
    fun prefix(sender: CommandSender, player: OnlinePlayer) {
        val api = LuckPermsProvider.get()
        val user = api.userManager.loadUser(player.player.uniqueId).join()

        user.getNodes()
            .filter { it.key.startsWith("prefix.143.") }
            .forEach { user.data().remove(it) }
        user.data().remove(Node.builder("suffix.143.").build())

        api.userManager.saveUser(user)

        val message = MiniMessage.miniMessage().deserialize("<yellow>Reset <gold>${player.player.name} <yellow>'s prefix<yellow>.")
        sender.sendMessage(message)
        TNTActingPluginMain.Managers.staffManager.sendLog(sender, message, "prefix")
    }

    @HelpCommand
    fun onHelp(sender: CommandSender, help: CommandHelp) {
        help.showHelp()
    }
}
