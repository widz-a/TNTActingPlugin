package wida.tntactingplugin.commands

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import org.bukkit.entity.Fireball
import org.bukkit.entity.Player
import wida.tntactingplugin.TNTActingPluginMain

class FireballCommand : TNTActingPluginMain.PluginCommand() {
    @CommandAlias("fireball")
    @CommandPermission("tnt.commands.fireball")
    @Description("Launch fireball")
    fun fireball(player: Player) {
        player.launchProjectile(Fireball::class.java)
    }
}