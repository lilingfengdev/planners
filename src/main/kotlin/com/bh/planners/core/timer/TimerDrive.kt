package com.bh.planners.core.timer

import com.bh.planners.api.event.PluginReloadEvent
import com.bh.planners.util.files
import com.germ.germplugin.api.GermKeyAPI
import com.germ.germplugin.api.KeyType
import eos.moe.dragoncore.api.CoreAPI
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.configuration.Configuration

object TimerDrive {

    val templates = mutableListOf<Template>()

    private val germKeys = mutableSetOf<String>()
    private val dragonKeys = mutableSetOf<String>()

    @Awake(LifeCycle.ENABLE)
    fun loadTemplate() {
        templates.clear()
        files("timer", listOf("timer_def0.yml", "select_job.yml")) {
            val configFile = Configuration.loadFromFile(it)
            templates += Template(it.name.replace(".yml", ""), configFile)
        }
        unRegKeys()
        regKeys()
    }

    @SubscribeEvent
    fun e(e: PluginReloadEvent) {
        this.loadTemplate()
    }

    fun getTemplates(timer: Timer<*>): List<Template> {
        return templates.filter { timer.name in it.triggers }
    }

    private fun unRegKeys() {
        if (Bukkit.getPluginManager().isPluginEnabled("GermPlugin")) {
            germKeys.forEach {
                GermKeyAPI.unregisterKey(KeyType.valueOf(it))
            }
        }
        /*
        if (Bukkit.getPluginManager().isPluginEnabled("DragonCore")) {
            dragonKeys.forEach {
                CoreAPI.unregisterKey(it)
            }
        }
         */
    }

    private fun regKeys() {
        templates.forEach {
            if (Bukkit.getPluginManager().isPluginEnabled("GermPlugin")) {
                if (it.root.name == "germ key up" || it.root.name == "germ key down") {
                    it.root.getString("__option__.key")?.let { key -> germKeys.add(key) }
                }
                GermKeyAPI.getRegisteredKeys().forEach { key ->
                    germKeys.remove(key.name)
                }
                germKeys.forEach {
                    GermKeyAPI.registerKey(KeyType.valueOf(it))
                }
            }
            if (Bukkit.getPluginManager().isPluginEnabled("DragonCore")) {
                if (it.root.name == "dragon key release" || it.root.name == "dragon key press") {
                    it.root.getString("__option__.key")?.let { key -> dragonKeys.add(key) }
                }
                dragonKeys.forEach {
                    CoreAPI.registerKey(it)
                }
            }
        }
    }

}

