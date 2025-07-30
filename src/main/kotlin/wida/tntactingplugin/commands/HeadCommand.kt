package wida.tntactingplugin.commands

import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Optional
import co.aikar.commands.annotation.Syntax
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import com.destroystokyo.paper.profile.ProfileProperty
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.mineskin.JsoupRequestHandler
import org.mineskin.MineSkinClient
import org.mineskin.data.Visibility
import org.mineskin.request.GenerateRequest
import wida.tntactingplugin.TNTActingPluginMain
import java.net.URI
import java.util.*

class HeadCommand : TNTActingPluginMain.PluginCommand() {
    private val CLIENT = MineSkinClient.builder()
        .requestHandler { a, b, c, d, e -> JsoupRequestHandler(a, b, c, d, e) }
        .userAgent("TNTActing/v1.0")
        .apiKey(plugin.getTNTConfig().getMineSkinAPIKey())
        .build()

    @CommandAlias("skull")
    @CommandPermission("tnt.commands.skull")
    @CommandCompletion("@offlineplayers")
    @Syntax("[<source: Name|URL>] [<amount: Number>] [<targetPlayer: Player>]")
    @Description("Get a player's skull")
    fun head(player: Player, @Optional _source: String?, @Default("1") amount: Int, @Optional onlinePlayer: OnlinePlayer?) {
        if (amount !in 1..64) throw InvalidCommandArgument("amount must be between 1 to 64")
        val source = _source ?: player.name
        val target = onlinePlayer?.player ?: player
        val isURI = try {
            val uri = URI.create(source)
            uri.scheme != null && uri.host != null
        } catch (_: Exception) {
            false
        }
        /*if (!isURI) {
            val head = ItemStack(Material.PLAYER_HEAD, amount)

            head.editMeta(SkullMeta::class.java) {
                it.owningPlayer = Bukkit.getOfflinePlayer(source)
            }
            target.inventory.addItem(head)
            player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Gave <gold>${target.name} ${source}<yellow>'s head."))
            return
        }*/
        player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Getting the skin using <gold>mineskin.org<yellow>."))

        val request = GenerateRequest.url(if (isURI) source else "https://wida.dev/skin/$source")
            .name("ga")
            .visibility(Visibility.UNLISTED)

        CLIENT.queue().submit(request)
            .thenCompose {
                // wait for job completion
                return@thenCompose it.job.waitForCompletion(CLIENT)
            }
            .thenCompose {
                // get skin from job or load it from the API
                return@thenCompose it.getOrLoadSkin(CLIENT);
            }

            .thenAccept {
                val data = it.texture().data()
                val profile = Bukkit.createProfile(UUID.randomUUID())
                profile.setProperty(ProfileProperty("textures", data.value, data.signature))

                val head = ItemStack(Material.PLAYER_HEAD, amount)
                head.editMeta(SkullMeta::class.java) { meta ->
                    meta.playerProfile = profile
                }
                target.inventory.addItem(head)

                player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Gave <gold>${target.name} <yellow>the skull."))
            }
            .exceptionally { throwable ->
                throwable.printStackTrace()
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Something went wrong!"))
                return@exceptionally null
            }

    }
}