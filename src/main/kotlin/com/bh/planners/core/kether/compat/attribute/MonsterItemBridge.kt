package com.bh.planners.core.kether.compat.attribute

import me.skymc.customized.monsteritem.attribute.Source
import me.skymc.customized.monsteritem.attribute.function.FunctionProfile
import org.bukkit.entity.LivingEntity
import java.util.*

class MonsterItemBridge : AttributeBridge {

    override fun addAttributes(uuid: UUID, timeout: Long, reads: List<String>) {
        this.addAttributes(UUID.randomUUID().toString(),uuid, timeout, reads)
    }

    override fun addAttributes(source: String, uuid: UUID, timeout: Long, reads: List<String>) {
        val profile = FunctionProfile.getMobProfileByUUID(uuid)
        val monsterItemSource = Source()
//        monsterItemSource.add()
//        profile!!.putAttributeSource(source,object : Source)
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

    override fun get(uuid: UUID, keyword: String): Any {
        TODO("Not yet implemented")
    }
}