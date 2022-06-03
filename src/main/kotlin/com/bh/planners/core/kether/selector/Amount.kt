package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.pojo.Session
import taboolib.common5.Coerce

object Amount : Selector {
    override val names: Array<String>
        get() = arrayOf("amount", "size")

    override fun check(target: Target?, args: String, session: Session, container: Target.Container) {
        val value = Coerce.toInteger(args)
        if (value > container.size) {
            container.remove(container.size - value)
        }
    }
}
