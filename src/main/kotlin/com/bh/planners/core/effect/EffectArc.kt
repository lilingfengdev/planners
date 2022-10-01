package com.bh.planners.core.effect

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.common.PlayerFrontCoordinate
import com.bh.planners.core.kether.game.ActionEffect
import org.bukkit.Location
import org.bukkit.util.Vector
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

object EffectArc : Effect() {

    override val name: String
        get() = "arc"

    val EffectOption.startAngle: Double
        get() = Coerce.toDouble(this.demand.get("start", "0.0"))

    val EffectOption.angle: Double
        get() = Coerce.toDouble(this.demand.get(Effects.ANGLE, "1.0"))

    val EffectOption.radius: Double
        get() = Coerce.toDouble(this.demand.get(Effects.RADIUS, "1.0"))

    val EffectOption.step: Double
        get() = Coerce.toDouble(this.demand.get(Effects.STEP, "1.0"))

    val EffectOption.spread: Double
        get() = Coerce.toDouble(this.demand.get("spread", "0.0"))

    val EffectOption.slope: Double
        get() = Coerce.toDouble(this.demand.get("slope", "0.0"))

    // 粒子渲染周期间隔
    val EffectOption.period: Long
        get() = Coerce.toLong(this.demand.get(listOf("period", "p"), "0"))

    val EffectOption.rotateAxisX: Double
        get() = Coerce.toDouble(this.demand.get(listOf("rax", "rotateAxisX", "0")))

    val EffectOption.rotateAxisY: Double
        get() = Coerce.toDouble(this.demand.get(listOf("rax", "rotateAxisY", "0")))

    val EffectOption.rotateAxisZ: Double
        get() = Coerce.toDouble(this.demand.get(listOf("rax", "rotateAxisZ", "0")))


    override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {
        val effectSpawner = EffectSpawner(option)
        option.createContainer(target, context).thenAccept { container ->
            val spread = option.spread
            val angle = option.angle
            val radius = option.radius
            val step = option.step
            val slope = option.slope
            container.forEachLocation {
                val coordinate = PlayerFrontCoordinate(this)
                val locations = mutableListOf<Location>()

                var i = 0.0

                while (if (angle <= -1) i > angle else i < angle) {
                    val radians = Math.toRadians(i + option.startAngle)
                    val vector = Vector()
                    vector.x = (radius + abs(i) / step * spread) * cos(radians)
                    vector.z = (radius + abs(i) / step * spread) * sin(radians)
                    vector.y = abs(i) / step * slope
                    rotateAxisVector(option, vector)
                    locations += coordinate.newLocation(vector.x, vector.y, vector.z)
                    if (angle <= -1) {
                        i -= step
                    } else {
                        i += step
                    }
                }

                if (option.period == 0L) {
                    locations.forEach(effectSpawner::spawn)
                    response.onTick(locations)
                } else {
                    var pointer = 0
                    submit(async = true, period = option.period) {
                        if (pointer == locations.size) {
                            cancel()
                            return@submit
                        }
                        val location = locations[pointer++]
                        response.onTick(listOf(location))
                        effectSpawner.spawn(location)
                    }
                }
            }

        }
    }

    fun rotateAxisVector(option: EffectOption, vector: Vector) {
        rotateAroundAxisZ(vector, option.rotateAxisZ)
        rotateAroundAxisX(vector, option.rotateAxisX)
        rotateAroundAxisY(vector, option.rotateAxisY)
    }

}
