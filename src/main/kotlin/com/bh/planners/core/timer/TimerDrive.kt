package com.bh.planners.core.timer

import com.bh.planners.api.event.PluginReloadEvent
import com.bh.planners.util.files
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.configuration.Configuration

object TimerDrive {

    val templates = mutableListOf<Template>()

    @Awake(LifeCycle.ENABLE)
    fun loadTemplate() {
        templates.clear()
        files("timer", listOf("timer_def0.yml","select_job.yml")) {
            val configFile = Configuration.loadFromFile(it)
            templates += Template(it.name.replace(".yml", ""), configFile)
        }
    }

    @SubscribeEvent
    fun e(e: PluginReloadEvent) {
        this.loadTemplate()
    }

    fun getTemplates(timer: Timer<*>): List<Template> {
        return templates.filter { timer.name in it.triggers }
    }

}

