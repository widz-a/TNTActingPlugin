package withicality.plugintemplate

import co.aikar.commands.BaseCommand
import co.aikar.commands.PaperCommandManager
import org.bukkit.event.Listener
import org.mineacademy.fo.ReflectionUtil
import org.mineacademy.fo.plugin.SimplePlugin
import pl.mikigal.config.Config
import pl.mikigal.config.ConfigAPI
import pl.mikigal.config.style.CommentStyle
import pl.mikigal.config.style.NameStyle


class PluginTemplatePlugin : SimplePlugin() {
    private val configs = HashMap<Class<out Config>, Config>()

    override fun onPluginStart() {
        registerConfigs()
        registerAllEvents(PluginListener::class.java)

        val cmdManager = PaperCommandManager(this)
        registerACFCommands(cmdManager)

    }

    abstract class PluginCommand : BaseCommand()
    open class PluginListener : Listener {
        protected val plugin = getInstance() as PluginTemplatePlugin
    }

    private fun registerACFCommands(manager: PaperCommandManager, extendingClass: Class<out BaseCommand> = PluginCommand::class.java) {
        ReflectionUtil.getClasses(this, extendingClass).filter { x -> x.canonicalName != extendingClass.canonicalName }.forEach { clazz ->
            val command = clazz.getDeclaredConstructor().newInstance() as BaseCommand
            manager.registerCommand(command)
        }
    }

    fun registerConfigs() {
        ReflectionUtil.getClasses(this, Config::class.java).filter { x -> x.canonicalName != Config::class.java.canonicalName }.forEach { clazz ->
            val config = ConfigAPI.init(
                clazz,
                NameStyle.CAMEL_CASE,
                CommentStyle.ABOVE_CONTENT,
                true,
                this
            )
            configs[clazz] = config
        }
    }

    fun <T : Config> getConfig(clazz: Class<T>): T {
        return configs[clazz] as T
    }

}