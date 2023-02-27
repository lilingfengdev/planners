package com.bh.planners.core.kether.compat.attribute

import ac.github.oa.internal.core.attribute.AttributeData
import com.bh.planners.api.common.SimpleTimeoutTask
import com.bh.planners.api.common.SimpleUniqueTask
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.serverct.ersha.api.AttributeAPI
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import java.util.*
import kotlin.math.max

class AttributePlus3Bridge : AttributeBridge {

    val LivingEntity.getAttributeData
        get() = AttributeAPI.getAttrData(this)

    override fun addAttributes(uuid: UUID, timeout: Long, reads: List<String>) {
        addAttributes(UUID.randomUUID().toString(), uuid, timeout, reads)
    }

    override fun addAttributes(source: String, uuid: UUID, timeout: Long, reads: List<String>) {
        val entity = Bukkit.getEntity(uuid) as? LivingEntity ?: return
        val attributeSource = AttributeAPI.getAttributeSource(reads)

        AttributeAPI.addSourceAttribute(entity.getAttributeData, source, attributeSource)
        if (timeout != -1L) {
            SimpleUniqueTask.submit("$uuid:$source", timeout / 50) {
                removeAttributes(uuid, source)
            }
        }
    }

    override fun removeAttributes(uuid: UUID, source: String) {
        SimpleUniqueTask.remove("$uuid:$source")
        val entity = Bukkit.getEntity(uuid) as? LivingEntity ?: error("null")
        AttributeAPI.takeSourceAttribute(entity.getAttributeData,source)
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