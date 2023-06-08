package com.bh.planners.core.pojo.key

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.event.PluginReloadEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

object KeySlotLoader {

    @Config("key.yml")
    lateinit var config: Configuration

    @Awake(LifeCycle.ENABLE)
    fun loadKeySlot() {
        PlannersAPI.keySlots.clear()
        config.getKeys(false).forEach {
            val section = config.getConfigurationSection(it)!!
            PlannersAPI.keySlots += KeySlot(section)
        }
        PlannersAPI.keySlots.sortBy { it.sort }
    }

    @SubscribeEvent
    fun e(e: PluginReloadEvent) {
        config.reload()
        this.loadKeySlot()
    }

}