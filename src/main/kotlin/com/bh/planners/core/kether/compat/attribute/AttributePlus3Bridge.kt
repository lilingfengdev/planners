package com.bh.planners.core.kether.compat.attribute

import ac.github.oa.internal.core.attribute.AttributeData
import com.bh.planners.api.common.SimpleTimeoutTask
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.serverct.ersha.api.AttributeAPI
import taboolib.common.platform.function.submit
import java.util.*
import kotlin.math.max

class AttributePlus3Bridge : AttributeBridge {

    val cache = mutableMapOf<LivingEntity, MutableList<Data>>()

    val task = submit(async = true, period = 20) {
        cache.forEach {
            val filter = it.value.filter { !it.isValid }
            if (filter.isNotEmpty()) {
                filter.forEach { data ->
                    AttributeAPI.takeSourceAttribute(it.key.getAttributeData, data.source)
                    cache[it.key]!!.remove(data)
                }
            }
        }

    }

    val LivingEntity.getAttributeData
        get() = AttributeAPI.getAttrData(this)

    fun getCache(livingEntity: LivingEntity): MutableList<Data> {
        return cache.computeIfAbsent(livingEntity) { mutableListOf() }
    }

    override fun addAttributes(uuid: UUID, timeout: Long, reads: List<String>) {
        addAttributes(UUID.randomUUID().toString(), uuid, timeout, reads)
    }

    override fun addAttributes(source: String, uuid: UUID, timeout: Long, reads: List<String>) {
        val entity = Bukkit.getEntity(uuid) as? LivingEntity ?: error("null")
        val attributeSource = AttributeAPI.getAttributeSource(reads)
        getCache(entity) += Data(source, timeout)
        AttributeAPI.addSourceAttribute(entity.getAttributeData, source, attributeSource)
    }

    override fun removeAttributes(uuid: UUID, source: String) {
        val entity = Bukkit.getEntity(uuid) as? LivingEntity ?: error("null")
        getCache(entity).removeIf { it.source == source }
    }

    override fun update(entity: LivingEntity) {

    }

    override fun update(uuid: UUID) {

    }

    override fun get(uuid: UUID, keyword: String): Any {
        return 0
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