package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.ActionTarget.Companion.getTarget
import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.pojo.Session

object Target : Selector {
    override val names: Array<String>
        get() = arrayOf("target", "t")

    override fun check(name: String, target: Target?, args: String, session: Session, container: Target.Container) {
        container.add(session.getTarget() ?: error("Not target."))
    }
}