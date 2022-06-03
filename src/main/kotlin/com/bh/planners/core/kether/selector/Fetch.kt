package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.ActionSelector
import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.pojo.Session

/**
 * 合并目标容器
 * -@fetch t0
 */
object Fetch : Selector {
    override val names: Array<String>
        get() = arrayOf("get", "fetch")

    override fun check(target: Target?, args: String, session: Session, container: Target.Container) {
        val signTargetContainer = ActionSelector.getContainer(session, args)
        if (signTargetContainer != null) {
            container.merge(signTargetContainer)
        }
    }
}