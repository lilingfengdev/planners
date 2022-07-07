package com.bh.planners.api

import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import taboolib.common.platform.Schedule
import java.util.UUID

object EntityAPI {

    private val map = mutableMapOf<UUID, Long>()
    private val waitRemove = mutableListOf<Entity>()

    fun register(uuid: UUID, timeout: Long) {
        map[uuid] = timeout
    }

    fun register(entity: Entity, timeout: Long) {
        register(entity.uniqueId, timeout)
    }

    @Schedule(delay = 20, period = 20, async = true)
    fun task() {
        val removed = mutableListOf<UUID>()
        val timeMillis = System.currentTimeMillis()
        map.forEach {
            if (it.value < timeMillis) {
                waitRemove += Bukkit.getEntity(it.key) ?: return@forEach
                removed += it.key
            }
        }
        removed.forEach { map.remove(it) }
    }
    @Schedule(delay = 20, period = 20, async = false)
    fun task0() {
        waitRemove.removeAll {
            it.remove()
            true
        }
    }

}