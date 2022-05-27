package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.kether.effect.Target.Companion.toTarget
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common5.Coerce

object Location : Selector {
    override val names: Array<String>
        get() = arrayOf("loc", "location", "l")

    override fun check(args: String, sender: Player, container: Target.Container) {
        container.addAll(args.split(",").map { it.inferLocation().toTarget() })
    }

    fun String.inferLocation(): Location {
        val split = split(",")
        return Location(
            Bukkit.getWorld(split[0]),
            Coerce.toDouble(split[1]),
            Coerce.toDouble(split[2]),
            Coerce.toDouble(split[3])
        )
    }


}
