package wida.tntactingplugin.managers

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.event.user.UserDataRecalculateEvent
import net.luckperms.api.model.user.User
import org.bukkit.Bukkit
import org.bukkit.scoreboard.DisplaySlot
import org.mineacademy.fo.remain.Remain
import wida.tntactingplugin.TNTActingPluginMain
import wida.tntactingplugin.utils.ComponentUtils

class TabManager(val plugin: TNTActingPluginMain) {
    fun init() {
        registerHeaderFooter()
        prefixEvent()
    }

    fun registerHeaderFooter() {
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            Remain.getOnlinePlayers().forEach {
                val header = PlaceholderAPI.setPlaceholders(it, plugin.getTNTConfig().getTabHeader())
                val footer = PlaceholderAPI.setPlaceholders(it, plugin.getTNTConfig().getTabFooter())
                it.sendPlayerListHeaderAndFooter(
                    MiniMessage.miniMessage().deserialize(header),
                    MiniMessage.miniMessage().deserialize(footer)
                )
            }
        }, 0L, 20L)
    }

    fun updatePrefix(user: User) {
        val player = Bukkit.getPlayer(user.uniqueId) ?: return
        //Ping
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val objective = scoreboard.getObjective("ping") ?: scoreboard.registerNewObjective("ping", "dummy", Component.text("Ping"))
        objective.displaySlot = DisplaySlot.PLAYER_LIST

        for (player in Bukkit.getOnlinePlayers()) {
            objective.getScore(player.name).score = player.ping
        }

        // Name
        val prefix = ComponentUtils.cmiToMiniMessage(PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix%"))
        val suffix = ComponentUtils.cmiToMiniMessage(PlaceholderAPI.setPlaceholders(player, "%luckperms_suffix%"))

        if (prefix.isEmpty()) return
        player.playerListName(MiniMessage.miniMessage().deserialize(
            "$prefix <reset>$suffix${player.name}"
        ))
    }

    fun prefixEvent() {
        val api = LuckPermsProvider.get()
        api.eventBus.subscribe(plugin, UserDataRecalculateEvent::class.java) {
            updatePrefix(it.user)
        }
    }
}