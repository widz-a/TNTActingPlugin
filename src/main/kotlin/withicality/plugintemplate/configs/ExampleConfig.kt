package withicality.plugintemplate.configs

import pl.mikigal.config.Config
import pl.mikigal.config.annotation.ConfigName

@JvmDefaultWithoutCompatibility
@ConfigName("config")
interface ExampleConfig : Config {
    fun getJoinMessage(): String {
        return "<aqua>No way he joined."
    }
}