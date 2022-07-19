package com.bh.planners.core.kether.compat.attribute

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI.addAttribute
import com.skillw.attsystem.api.AttrAPI.read
import com.skillw.attsystem.api.AttrAPI.removeAttribute
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.submit
import java.util.*
import kotlin.math.max

class AttributeSystemBridge : AttributeBridge {
    val cache = mutableMapOf<LivingEntity, MutableList<Data>>()

    val task = submit(async = true, period = 20) {
        cache.forEach {
            it.value.filter { data -> !data.isValid }.forEach { data ->
                it.key.removeAttribute(data.source)
                cache[it.key]!!.remove(data)
            }
        }
    }

    fun getCache(livingEntity: LivingEntity): MutableList<Data> {
        return cache.computeIfAbsent(livingEntity) { mutableListOf() }
    }

    override fun addAttributes(uuid: UUID, timeout: Long, reads: List<String>) {
        addAttributes(UUID.randomUUID().toString(), uuid, timeout, reads)
    }

    override fun addAttributes(source: String, uuid: UUID, timeout: Long, reads: List<String>) {
        val entity = Bukkit.getEntity(uuid) as? LivingEntity ?: error("null")
        val attributeData = reads.read(entity).unRelease()
        getCache(entity) += Data(source, timeout)
        uuid.addAttribute(source, attributeData)
    }

    override fun removeAttributes(uuid: UUID, source: String) {
        val entity = Bukkit.getEntity(uuid) as? LivingEntity ?: error("null")
        getCache(entity).removeIf { it.source == source }
    }

    override fun update(entity: LivingEntity) {
        AttributeSystem.attributeSystemAPI.update(entity)
    }

    override fun update(uuid: UUID) {
        AttributeSystem.attributeSystemAPI.update(uuid)
    }

    class Data(val source: String, val timeout: Long) {

        val create = System.currentTimeMillis()

        val end: Long
            get() = timeout + create

        val countdown: Long
            get() = max(end - System.currentTimeMillis(), 0)

        val isValid: Boolean
            get() = countdown > 0L

    }
}