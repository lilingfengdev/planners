package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.pojo.Session
import org.bukkit.entity.Player

object Type : Selector {
    override val names: Array<String>
        get() = arrayOf("type", "t")

    override fun check(args: String, session: Session, sender: Player, container: Target.Container) {
        val types = args.split(",")
        container.removeIf {
            if (this is Target.Entity) {
                livingEntity.type.name !in types
            } else true
        }
    }


}
