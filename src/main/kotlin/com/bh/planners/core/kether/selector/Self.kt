package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.selector.Team.isNon
import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.skill.effect.Target.Companion.toTarget
import taboolib.common.platform.function.info
import taboolib.platform.type.BukkitPlayer

/**
 * 选中自己
 * -@self
 * -@this
 */
object Self : Selector {

    override val names: Array<String>
        get() = arrayOf("self", "this", "!self", "!this")

    override fun check(name: String, target: Target?, args: String, session: Session, container: Target.Container) {
        val executor = session.executor as? BukkitPlayer ?: return
        val entity = executor.player.toTarget()
        if (name.isNon()) {
            container.removeIf { this == entity }
        } else {
            container.add(entity)
        }
    }

}
