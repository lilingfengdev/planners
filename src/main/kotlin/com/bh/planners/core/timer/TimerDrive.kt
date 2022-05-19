package com.bh.planners.core.timer

import com.bh.planners.core.timer.impl.TimerRunnable
import com.bh.planners.util.files
import org.bukkit.event.Event
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.reflect.Reflex.Companion.invokeConstructor
import taboolib.module.configuration.Configuration
import java.sql.Time

object TimerDrive {

    val templates = mutableListOf<Template>()

    @Awake(LifeCycle.ENABLE)
    fun loadTimer() {
        templates.clear()
        files("timer", listOf("timer_def0.yml")) {
            val configFile = Configuration.loadFromFile(it)
            templates += Template(it.name.replace(".yml", ""), configFile)
        }
    }


}

