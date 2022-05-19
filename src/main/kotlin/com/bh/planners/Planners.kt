package com.bh.planners

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

object Planners : Plugin() {

    @Config("config.yml")
    lateinit var config : Configuration

    override fun onEnable() {

    }
}
