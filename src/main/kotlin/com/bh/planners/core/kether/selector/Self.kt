package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.kether.effect.Target.Companion.ifEntity
import com.bh.planners.core.kether.effect.Target.Companion.ifLocation
import com.bh.planners.core.kether.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Session
import org.bukkit.entity.Player

/**
 * 选中自己
 * -@self
 * -@this
 */
object Self : Selector {

    override val names: Array<String>
        get() = arrayOf("self", "this")

    override fun check(target: Target?, args: String, session: Session, container: Target.Container) {
        container.add(target ?: return)
    }

}
