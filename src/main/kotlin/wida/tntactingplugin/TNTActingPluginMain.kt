package wida.tntactingplugin

import co.aikar.commands.BaseCommand
import co.aikar.commands.PaperCommandManager
import net.luckperms.api.LuckPermsProvider
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.event.Listener
import org.mineacademy.fo.ReflectionUtil
import org.mineacademy.fo.plugin.SimplePlugin
import pl.mikigal.config.ConfigAPI
import pl.mikigal.config.style.CommentStyle
import pl.mikigal.config.style.NameStyle
import wida.tntactingplugin.managers.StaffManager
import wida.tntactingplugin.managers.TabManager


class TNTActingPluginMain : SimplePlugin() {

    private var config: TNTConfig? = null
    fun getTNTConfig(): TNTConfig {
        return config!!
    }

    companion object {
        fun getInstance(): TNTActingPluginMain {
            return getPlugin(TNTActingPluginMain::class.java)
        }
    }

    object Managers {
        val staffManager = StaffManager()
        val tabManager = TabManager(getInstance())
    }

    override fun onPluginStart() {
        config = ConfigAPI.init(
            TNTConfig::class.java, NameStyle.CAMEL_CASE, CommentStyle.ABOVE_CONTENT, false, this
        )

        registerAllEvents(PluginListener::class.java)

        val cmdManager = PaperCommandManager(this)
        cmdManager.commandContexts.registerContext(OfflinePlayer::class.java) { c ->
            return@registerContext Bukkit.getOfflinePlayer(c.popFirstArg())
        }

        cmdManager.commandCompletions.registerAsyncCompletion("offlineplayers") { c ->
            Bukkit.getOnlinePlayers().mapNotNull { it.name }
        }

        cmdManager.commandCompletions.registerAsyncCompletion("lpgroups") { c ->
            LuckPermsProvider.get().groupManager.loadedGroups.filter { it.name != "default" }.mapNotNull { it.name }
        }
        registerACFCommands(cmdManager)

        Managers.tabManager.init()
    }

    abstract class PluginCommand : BaseCommand() {
        protected val plugin = getInstance()
    }
    open class PluginListener : Listener {
        protected val plugin = getInstance()
    }

    private fun registerACFCommands(manager: PaperCommandManager, extendingClass: Class<out BaseCommand> = PluginCommand::class.java) {
        ReflectionUtil.getClasses(this, extendingClass).filter { x -> x.canonicalName != extendingClass.canonicalName }.forEach { clazz ->
            val command = clazz.getDeclaredConstructor().newInstance() as BaseCommand
            manager.registerCommand(command)
        }
    }
}