package com.bh.planners.core.kether.effect.selector

import com.bh.planners.core.kether.effect.Target
import org.bukkit.entity.Player

object Type : Selector {
    override val names: Array<String>
        get() = arrayOf("type", "t")

    override fun check(args: String, sender: Player, container: Target.Container) {
        val types = args.split(",")
        container.removeIf {
            if (this is Target.Entity) {
                livingEntity.type.name !in types
            } else true
        }
    }


}
