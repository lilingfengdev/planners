package com.bh.planners.core.kether.selector

import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.skill.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Session
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import java.util.*

/**
 * 根据entity id来选中特殊实体
 * -@entity 230
 */
object EntityId : Selector {
    override val names: Array<String>
        get() = arrayOf("entity", "ei", "entityId")

    override fun check(name: String, target: Target?, args: String, session: Session, container: Target.Container) {
        args.split(",").forEach {
            val entity = Bukkit.getEntity(UUID.fromString(it)) as? LivingEntity ?: return@forEach
            container.add(entity.toTarget())
        }
    }
}
