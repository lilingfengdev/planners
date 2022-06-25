package com.bh.planners.core.kether.selector

import com.bh.planners.core.pojo.Session
import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.skill.effect.Target.Companion.toTarget
import com.bh.planners.core.skill.effect.common.PlayerFrontCoordinate
import taboolib.common5.Coerce
import kotlin.math.cos
import kotlin.math.sin

/**
 * -@pc-dot radius,y,angle
 * -@pc-dot 2,
 */
object CircleDot : Selector {

    override val names: Array<String>
        get() = arrayOf("c-dot", "cdot", "cd")

    override fun check(name: String, target: Target?, args: String, session: Session, container: Target.Container) {
        val location = target as? Target.Location ?: return
        val coordinate = PlayerFrontCoordinate(location.value)
        val split = args.split(",")
        val radius = Coerce.toDouble(split[0])
        val y = Coerce.toDouble(split.getOrElse(1) { "0" })
        val angle = Coerce.toDouble(split.getOrElse(2) { "0" })
        val radians = Math.toRadians(angle)
        val x: Double = radius * cos(radians)
        val z: Double = radius * sin(radians)
        container.add(coordinate.newLocation(x, y, z).toTarget())
    }
}