package wida.tntactingplugin

import co.aikar.commands.BaseCommand
import co.aikar.commands.PaperCommandManager
import net.luckperms.api.LuckPermsProvider
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.event.Listener
import org.mineacademy.fo.ReflectionUtil
import org.mineacademy.fo.plugin.SimplePlugin
import wida.tntactingplugin.manager.StaffManager


class TNTActingPluginMain : SimplePlugin() {

    object Managers {
        val staffManager = StaffManager()
    }

    override fun onPluginStart() {
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
    }

    abstract class PluginCommand : BaseCommand() {
        protected val plugin = getInstance() as TNTActingPluginMain
    }
    open class PluginListener : Listener {
        protected val plugin = getInstance() as TNTActingPluginMain
    }

    private fun registerACFCommands(manager: PaperCommandManager, extendingClass: Class<out BaseCommand> = PluginCommand::class.java) {
        ReflectionUtil.getClasses(this, extendingClass).filter { x -> x.canonicalName != extendingClass.canonicalName }.forEach { clazz ->
            val command = clazz.getDeclaredConstructor().newInstance() as BaseCommand
            manager.registerCommand(command)
        }
    }
}