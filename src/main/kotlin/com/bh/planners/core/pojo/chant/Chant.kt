package com.bh.planners.core.pojo.chant

import taboolib.library.configuration.ConfigurationSection

class Chant(val config: ConfigurationSection) {

    val builder = ChantBuilder.newInstance(config.getString("builder", "action bar")!!, config)

    val interrupts = config.getStringList("interrupt").map { Interrupt.getInterrupt(it) }

    fun isInterrupt(interrupt: Interrupt): Boolean {
        return interrupts.contains(interrupt)
    }

}