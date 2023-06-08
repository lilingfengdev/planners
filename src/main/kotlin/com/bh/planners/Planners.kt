package com.bh.planners

import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Platform
import taboolib.common.platform.Plugin
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.metrics.Metrics
import taboolib.platform.BukkitPlugin

@RuntimeDependency(
    value = "!org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3",
    test = "!kotlinx.coroutines.GlobalScope",
    initiative = true
)
object Planners : Plugin() {

    @Config("config.yml")
    lateinit var config: Configuration

    override fun onEnable() {
        Metrics(15573, BukkitPlugin.getInstance().description.version, Platform.BUKKIT)
    }
}
