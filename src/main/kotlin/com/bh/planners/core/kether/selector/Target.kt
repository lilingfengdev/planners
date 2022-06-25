package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.ActionTarget.Companion.getTarget
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.skill.effect.Target

object Target : Selector {
    override val names: Array<String>
        get() = arrayOf("target", "t")

    override fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container) {
        container.add(context.getTarget() ?: error("Not target."))
    }
}