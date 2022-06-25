package com.bh.planners.core.kether.selector

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.skill.effect.Target.Companion.toTarget
import taboolib.platform.type.BukkitPlayer

/**
 * 选中自己
 * -@self
 * -@this
 */
object Self : Selector {

    override val names: Array<String>
        get() = arrayOf("self", "this", "!self", "!this")

    override fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container) {
        val executor = context.executor as? BukkitPlayer ?: return
        val entity = executor.player.toTarget()
        if (name.isNon()) {
            container.removeIf { this == entity }
        } else {
            container.add(entity)
        }
    }

}
