package com.bh.planners.core.module

import taboolib.common.platform.function.info
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

object ModuleLoader {

    @Config("module.yml", autoReload = true)
    lateinit var config: Configuration

    fun selectedModule(name: String, target: String) {
        info("[Module | $name] Loaded module $target to the driver.")
    }

}