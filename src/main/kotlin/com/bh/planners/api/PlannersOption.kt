package com.bh.planners.api

import com.bh.planners.Planners
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration

object PlannersOption {

    val root: ConfigurationSection
        get() = Planners.config.getConfigurationSection("options")!!

    val scopeThreshold: List<Double>
        get() = root.getDoubleList("scope-threshold")

}
