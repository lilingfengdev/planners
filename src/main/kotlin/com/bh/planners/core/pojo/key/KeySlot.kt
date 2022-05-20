package com.bh.planners.core.pojo.key

import com.bh.planners.Planners
import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersOption
import com.bh.planners.api.event.PluginReloadEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.configuration.ConfigurationSection

class KeySlot(config: ConfigurationSection) : IKeySlot {

    override val key = config.name

    override val name = config.getString("name", key)!!

    companion object {

        @Awake(LifeCycle.ENABLE)
        fun loadKeySlot() {
            PlannersAPI.keySlots.clear()
            PlannersOption.root.getConfigurationSection("key-slot")?.getKeys(false)?.forEach {
                val section = PlannersOption.root.getConfigurationSection("key-slot.$it")!!
                PlannersAPI.keySlots += KeySlot(section)
            }
        }

        @SubscribeEvent
        fun e(e: PluginReloadEvent) {
            this.loadKeySlot()
        }

    }

}
