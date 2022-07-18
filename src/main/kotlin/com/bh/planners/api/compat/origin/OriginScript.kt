package com.bh.planners.api.compat.origin

import ac.github.oa.internal.core.attribute.impl.Script
import com.bh.planners.api.EntityAPI.getDataContainer
import com.bh.planners.api.EntityAPI.setFlag
import com.bh.planners.core.pojo.data.Data
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent

object OriginScript {

    val isEnable by lazy { Bukkit.getPluginManager().isPluginEnabled("OriginAttribute") }

    @Awake(LifeCycle.ACTIVE)
    fun setup() {
        if (!isEnable) return
        Script.registerStaticClass("planners", Hook)
    }


    object Hook {

        fun getContainer(entity: Entity) = entity.getDataContainer()

        fun getFlag(entity: Entity, key: String) = getContainer(entity)[key]?.data

        fun setFlag(entity: Entity, key: String, tick: Long, value: Any) {
            entity.setFlag(key, Data(value, survivalStamp = tick * 50))
        }


    }

}