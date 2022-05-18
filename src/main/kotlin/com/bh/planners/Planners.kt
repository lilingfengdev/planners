package com.bh.planners

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info

object Planners : Plugin() {

    override fun onEnable() {
        info("Successfully running ExamplePlugin!")
    }
}