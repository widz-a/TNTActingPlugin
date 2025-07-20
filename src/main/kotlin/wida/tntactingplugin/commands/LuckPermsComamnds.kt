package wida.tntactingplugin.commands

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Syntax
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import net.luckperms.api.node.Node
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import wida.tntactingplugin.TNTActingPluginMain
import kotlin.text.lowercase

class LuckPermsComamnds : TNTActingPluginMain.PluginCommand() {
    enum class Type { TRUE, FALSE, TOGGLE }

    @CommandAlias("rank")
    @CommandPermission("tnt.commands.rank")
    @CommandCompletion("@offlineplayers @lpgroups true|false|toggle")
    @Syntax("<player> <group> [true|false|toggle]")
    @Description("Set a player's rank")
    fun rank(sender: CommandSender, player: OfflinePlayer, rank: String, @Default("toggle") type: Type) {
        val api = LuckPermsProvider.get()
        val user = api.userManager.loadUser(player.uniqueId).join()
        val groupName = "group.${rank.lowercase()}"
        if (type == Type.TRUE) changeRank(api, user, sender, player, rank, true)
        if (type == Type.FALSE) changeRank(api, user, sender, player, rank, false)
        if (type == Type.TOGGLE) {
            val option = api.contextManager.staticQueryOptions
            changeRank(api, user, sender, player, rank,
                !user.cachedData.getPermissionData(option).checkPermission(groupName).asBoolean()
            )
        }
    }

    private fun changeRank(api: LuckPerms, user: User, sender: CommandSender, player: OfflinePlayer, rank: String, boolean: Boolean) {
        val groupName = "group.${rank.lowercase()}"
        val node = Node.builder(groupName).build()
        var message: Component
        if (boolean) {
            user.data().add(node)
            message = MiniMessage.miniMessage().deserialize("<yellow>Added <gold>${player.name} <yellow>to group <gold>${rank.lowercase()}<yellow>.")
        } else {
            user.data().remove(node)
            message = MiniMessage.miniMessage().deserialize("<yellow>Removed <gold>${player.name} <yellow>from group <gold>${rank.lowercase()}<yellow>.")
        }
        sender.sendMessage(message)
        TNTActingPluginMain.Managers.staffManager.sendLog(sender, message, "rank")
        api.userManager.saveUser(user)
    }
}