package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.pojo.Session

/**
 * 选中自己
 * -@self
 * -@this
 */
object Self : Selector {

    override val names: Array<String>
        get() = arrayOf("self", "this")

    override fun check(name: String, target: Target?, args: String, session: Session, container: Target.Container) {
        container.add(target ?: return)
    }

}
