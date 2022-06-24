package com.bh.planners.core.feature.attribute

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.core.attribute.AttributeData
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import java.util.*
import kotlin.math.max

class OriginAttributeBridge : AttributeBridge {


    override fun addAttributes(uuid: UUID, timeout: Long, reads: List<String>): String {
        return addAttributes(UUID.randomUUID().toString(), uuid, timeout, reads)
    }

    override fun addAttributes(source: String, uuid: UUID, timeout: Long, reads: List<String>): String {
        val data = Data(timeout)
        data.merge(OriginAttributeAPI.loadList(reads))
        OriginAttributeAPI.setExtra(uuid, source, data)
        return source
    }

    override fun update(entity: LivingEntity) {
        OriginAttributeAPI.callUpdate(entity)
    }

    override fun update(uuid: UUID) {
        this.update(Bukkit.getEntity(uuid) as? LivingEntity ?: return)
    }

    override fun removeAttributes(uuid: UUID, source: String) {
        OriginAttributeAPI.removeExtra(uuid, source)
    }

    class Data(val timeout: Long) : AttributeData() {

        val create = System.currentTimeMillis()

        val end: Long
            get() = timeout + create

        val countdown: Long
            get() = max(end - System.currentTimeMillis(), 0)

        override val isValid: Boolean
            get() = countdown > 0L

    }

}