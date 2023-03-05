package com.bh.planners.api

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.core.pojo.data.Data
import com.bh.planners.core.pojo.data.DataContainer
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import taboolib.common.platform.Schedule
import java.util.Collections
import java.util.UUID

object EntityAPI {

    private val map = Collections.synchronizedMap(mutableMapOf<UUID, DataContainer>())


    fun Entity.getDataContainer(): DataContainer {
        return if (this is Player) {
            plannersProfile.flags
        } else {
            map.computeIfAbsent(this.uniqueId) { DataContainer() }
        }
    }

    /**
     * 一小时清理一次无用实体
     */
    @Schedule(period = 20 * 60 * 60)
    fun task() {
        map.filter { Bukkit.getEntity(it.key) == null }.forEach {
            map.remove(it.key)
        }
    }

    fun Entity.get(key: String) = getFlag(key)

    fun Entity.getFlag(key: String): Data? {
        return getDataContainer()[key]
    }

    fun Entity.updateFlag(key: String, value: Any) {
        getDataContainer().update(key, value)
    }

    fun Entity.deleteFlag(key: String) {
        getDataContainer().remove(key)
    }

    fun Entity.setFlag(key: String, data: Data) {
        getDataContainer()[key] = data
    }
}