package wida.tntactingplugin.managers

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.event.user.UserDataRecalculateEvent
import net.luckperms.api.model.user.User
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.mineacademy.fo.remain.Remain
import wida.tntactingplugin.TNTActingPluginMain
import wida.tntactingplugin.utils.ComponentUtils

class TabManager(val plugin: TNTActingPluginMain) {
    fun init() {
        prefixEvent()
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            Remain.getOnlinePlayers().forEach {
                setHeaderFooter(it)
                updatePing(it)
            }
        }, 0L, 20L)
    }

    fun setHeaderFooter(player: Player) {
        val header = PlaceholderAPI.setPlaceholders(player, plugin.getTNTConfig().getTabHeader())
        val footer = PlaceholderAPI.setPlaceholders(player, plugin.getTNTConfig().getTabFooter())
        player.sendPlayerListHeaderAndFooter(
            MiniMessage.miniMessage().deserialize(header),
            MiniMessage.miniMessage().deserialize(footer)
        )
    }

    fun updatePing(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val objective = scoreboard.getObjective("ping") ?: scoreboard.registerNewObjective("ping", Criteria.DUMMY, Component.text("Ping"))
        objective.displaySlot = DisplaySlot.PLAYER_LIST
        objective.getScore(player.name).score = player.ping
    }

    fun updatePrefix(user: User) {
        val player = Bukkit.getPlayer(user.uniqueId) ?: return
        val prefix = ComponentUtils.cmiToMiniMessage(PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix%"))
        val suffix = ComponentUtils.cmiToMiniMessage(PlaceholderAPI.setPlaceholders(player, "%luckperms_suffix%"))

        if (prefix.isEmpty()) return
        val tab = "$prefix ${if (suffix.isEmpty()) "" else "<reset>$suffix"}${player.name}"
        //player.sendMessage(tab)
        player.playerListName(MiniMessage.miniMessage().deserialize(tab))
    }

    fun prefixEvent() {
        val api = LuckPermsProvider.get()
        api.eventBus.subscribe(plugin, UserDataRecalculateEvent::class.java) {
            updatePrefix(it.user)
        }
    }
}