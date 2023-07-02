package com.bh.planners.core.kether.compat.dragoncore

import taboolib.common.platform.Schedule

object RopeRender {

    val rendline = mutableMapOf<String, Rope>()

    @Schedule(async = true, period = 1)
    fun rendering() {
        rendline.forEach {
            it.value.rend()
        }
    }

}