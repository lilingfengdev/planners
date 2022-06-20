package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.selector.Team.isNon
import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.pojo.Session
import taboolib.common.platform.function.info

/**
 * 选中自己
 * -@self
 * -@this
 */
object Self : Selector {

    override val names: Array<String>
        get() = arrayOf("self", "this", "!self", "!this")

    override fun check(name: String, target: Target?, args: String, session: Session, container: Target.Container) {
        if (name.isNon()) {
            container.removeIf { (this == target)}
        } else {
            container.add(target ?: return)
        }
    }

}
