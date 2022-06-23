package com.bh.planners.api.combat.attribute

import org.bukkit.entity.LivingEntity
import java.util.*

class SXAttributeBridge : AttributeBridge{
    override fun addAttributes(uuid: UUID, timeout: Long, reads: List<String>): String {
        TODO("Not yet implemented")
    }

    override fun addAttributes(source: String, uuid: UUID, timeout: Long, reads: List<String>): String {
        TODO("Not yet implemented")
    }

    override fun removeAttributes(uuid: UUID, source: String) {
        TODO("Not yet implemented")
    }

    override fun update(entity: LivingEntity) {
        TODO("Not yet implemented")
    }

    override fun update(uuid: UUID) {
        TODO("Not yet implemented")
    }
}