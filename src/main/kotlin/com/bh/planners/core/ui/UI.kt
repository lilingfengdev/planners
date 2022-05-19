package com.bh.planners.core.ui

import com.bh.planners.api.event.PluginReloadEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

object UI {

    @Config("ui.yml")
    lateinit var config : Configuration

    @SubscribeEvent
    fun e(e : PluginReloadEvent) {
        config.reload()
    }


}