package com.bh.planners.core.kether.compat.attribute

import org.bukkit.entity.LivingEntity
import java.util.*

class SXAttributeBridge : AttributeBridge {
    override fun addAttributes(uuid: UUID, timeout: Long, reads: List<String>) {
    }

    override fun addAttributes(source: String, uuid: UUID, timeout: Long, reads: List<String>) {
    }

    override fun removeAttributes(uuid: UUID, source: String) {
    }

    override fun update(entity: LivingEntity) {
    }

    override fun update(uuid: UUID) {
    }

    override fun get(uuid: UUID, keyword: String): Any {
        return "__NULL__"
    }
}
