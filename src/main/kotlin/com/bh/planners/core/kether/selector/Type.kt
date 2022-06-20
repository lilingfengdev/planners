package com.bh.planners.core.kether.selector

import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.pojo.Session

/**
 * 选中指定实体
 * -@type PLAYER,ZOMBIE,...
 * -@t PLAYER,ZOMBIE,...
 */
object Type : Selector {
    override val names: Array<String>
        get() = arrayOf("type", "t", "!type", "!t")

    override fun check(name: String, target: Target?, args: String, session: Session, container: Target.Container) {
        val types = args.split(",")
        container.removeIf {
            if (this is Target.Entity) {
                if (name.isNon()) livingEntity.type.name in types else livingEntity.type.name !in types
            } else true
        }
    }


}
