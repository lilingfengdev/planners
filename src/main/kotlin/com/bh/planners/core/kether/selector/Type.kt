package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.pojo.Session

object Type : Selector {
    override val names: Array<String>
        get() = arrayOf("type", "t")

    override fun check(target: Target?, args: String, session: Session, container: Target.Container) {
        val types = args.split(",")
        container.removeIf {
            if (this is Target.Entity) {
                livingEntity.type.name !in types
            } else true
        }
    }


}
