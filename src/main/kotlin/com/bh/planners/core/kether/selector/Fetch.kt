package com.bh.planners.core.kether.selector

import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.data.Data

/**
 * 合并目标容器
 * -@fetch t0
 */
object Fetch : Selector {
    override val names: Array<String>
        get() = arrayOf("get", "fetch")

    fun Data.asContainer(): Target.Container {
        return data as Target.Container
    }

    override fun check(name: String, target: Target?, args: String, session: Session, container: Target.Container) {
        container.merge(session.flags[args]?.asContainer() ?: Target.Container())
    }
}