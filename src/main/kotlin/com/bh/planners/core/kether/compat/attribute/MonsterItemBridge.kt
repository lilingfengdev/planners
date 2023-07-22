package com.bh.planners.core.kether.compat.attribute

import me.skymc.customized.monsteritem.api.event.AttributeLoadedEvent
import me.skymc.customized.monsteritem.attribute.Attribute
import me.skymc.customized.monsteritem.attribute.Source
import me.skymc.customized.monsteritem.attribute.Value
import me.skymc.customized.monsteritem.attribute.function.FunctionProfile
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.info
import taboolib.common.platform.function.registerBukkitListener
import java.util.*

class MonsterItemBridge : AttributeBridge {

    val listener = registerBukkitListener(AttributeLoadedEvent.Post::class.java) { event ->
        info("attribute loaded post")
        AttributeBridge.updateJob(event.player)
        AttributeBridge.updateSkill(event.player)
    }

    override fun addAttributes(uuid: UUID, timeout: Long, reads: List<String>) {
        this.addAttributes(UUID.randomUUID().toString(), uuid, timeout, reads)
    }

    override fun addAttributes(source: String, uuid: UUID, timeout: Long, reads: List<String>) {
        info("add attribute pre $reads")
        val profile = FunctionProfile.getPlayerProfileByUUID(uuid) ?: return
        val monsterItemSource = Source()
        reads.forEach {
            val split = it.split(":")
            val id = split[0].trim().uppercase(Locale.getDefault())
            val value = split[1].trim()
            monsterItemSource.addValue(Attribute.valueOf(id), Value.read(value)!!)
        }
        if (timeout == -1L) {
            info("add attributes $uuid $profile $source $monsterItemSource")
            profile.putAttributeSource(source, monsterItemSource, true)
        } else {
            info("add attributes $uuid $profile $source $timeout $monsterItemSource")
            profile.putTemporaryAttributeSource(source, monsterItemSource, timeout)
        }
    }

    override fun removeAttributes(uuid: UUID, source: String) {
        FunctionProfile.getMobProfileByUUID(uuid)?.removeAttributeSource(source)
    }

    override fun update(entity: LivingEntity) {
    }

    override fun update(uuid: UUID) {
    }

    override fun get(uuid: UUID, keyword: String): Any {
        return FunctionProfile.getMobProfileByUUID(uuid)?.getAttributeValue(Attribute.valueOf(keyword))?.print() ?: "__NULL__"
    }
}