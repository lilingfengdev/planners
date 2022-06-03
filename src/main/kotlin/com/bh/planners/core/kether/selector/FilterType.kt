package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.pojo.Session

/**
 * 过滤指定类型
 * -@filtertype PLAYER,ZOMBIE,...
 * -@filterType PLAYER,ZOMBIE,...
 * -@ft PLAYER,ZOMBIE,...
 */
object FilterType : Selector {
    override val names: Array<String>
        get() = arrayOf("filtertype", "filterType", "ft")

    override fun check(target: Target?, args: String, session: Session, container: Target.Container) {
        val types = args.split(",")
        container.removeIf {
            if (this is Target.Entity) {
                livingEntity.type.name in types
            } else false
        }
    }
}
