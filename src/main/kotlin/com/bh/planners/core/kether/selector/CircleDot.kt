package com.bh.planners.core.kether.selector

import com.bh.planners.core.pojo.Session
import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.skill.effect.Target.Companion.toTarget
import com.bh.planners.core.skill.effect.common.PlayerFrontCoordinate
import taboolib.common5.Coerce
import kotlin.math.cos
import kotlin.math.sin

/**
 * -@c-dot radius,angle
 * -@c-dot 2,
 */
object CircleDot : Selector {

    override val names: Array<String>
        get() = arrayOf("c-dot", "cdot", "cd")

    override fun check(name: String, target: Target?, args: String, session: Session, container: Target.Container) {
        val location = target as? Target.Location ?: return
        val coordinate = PlayerFrontCoordinate(location.value)
        val split = args.split(",")
        val radius = Coerce.toDouble(split[0])
        val angle = Coerce.toDouble(if (split.size == 2) split[1] else "0")
        val radians = Math.toRadians(angle)
        val x: Double = radius * cos(radians)
        val z: Double = radius * sin(radians)
        container.add(coordinate.newLocation(x, 0.0, z).toTarget())
    }
}