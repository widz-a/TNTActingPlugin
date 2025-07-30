package wida.tntactingplugin

import pl.mikigal.config.Config
import pl.mikigal.config.annotation.Comment
import pl.mikigal.config.annotation.ConfigName
import wida.tntactingplugin.utils.ComponentUtils

@JvmDefaultWithoutCompatibility
@ConfigName("config.yml")
interface TNTConfig : Config {
    @Comment("Using MiniMessage. https://webui.advntr.dev/")
    fun getTabHeader(): String {
        return ComponentUtils.mojangToMiniMessage("&cTNT&fActing")
    }

    fun getTabFooter(): String {
        return ComponentUtils.mojangToMiniMessage("&cOnline: &6%server_online%&7/&680")
    }

    fun getMineSkinAPIKey(): String {
        return ""
    }
}