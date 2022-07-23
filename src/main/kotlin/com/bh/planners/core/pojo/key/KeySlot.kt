package com.bh.planners.core.pojo.key

import com.bh.planners.Planners
import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersOption
import com.bh.planners.api.event.PluginReloadEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

class KeySlot(val config: ConfigurationSection) : IKeySlot {

    override val key = config.name

    override val group = config.getString("group", key)!!

    override val groups: List<Int>
        get() = config.getIntegerList("groups")

    override val name = config.getString("name", key)!!

    override val sort = config.getLong("sort", 1)

    override val description = config.getStringList("description")

}
