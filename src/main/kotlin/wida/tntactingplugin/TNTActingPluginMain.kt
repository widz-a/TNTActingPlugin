package wida.tntactingplugin

import co.aikar.commands.BaseCommand
import co.aikar.commands.PaperCommandManager
import org.bukkit.event.Listener
import org.mineacademy.fo.ReflectionUtil
import org.mineacademy.fo.plugin.SimplePlugin


class TNTActingPluginMain : SimplePlugin() {

    override fun onPluginStart() {
        registerAllEvents(PluginListener::class.java)

        val cmdManager = PaperCommandManager(this)
        registerACFCommands(cmdManager)

    }

    abstract class PluginCommand : BaseCommand()
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