package com.bh.planners

import org.bukkit.entity.EntityType
import taboolib.common.platform.Platform
import taboolib.common.platform.Plugin
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.metrics.Metrics
import taboolib.platform.BukkitPlugin

object Planners : Plugin() {

    @Config("config.yml")
    lateinit var config: Configuration

    override fun onEnable() {
        Metrics(15573, BukkitPlugin.getInstance().description.version, Platform.BUKKIT)
    }
}
