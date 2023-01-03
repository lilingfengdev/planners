package com.bh.planners.core.effect

import com.bh.planners.api.common.ParticleFrame
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.common.PlayerFrontCoordinate
import com.bh.planners.core.kether.game.ActionEffect
import org.bukkit.Location
import org.bukkit.util.Vector
import taboolib.common5.Coerce
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

object EffectArc : Effect() {

    override val name: String
        get() = "arc"



    override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {
        val spawner = EffectSpawner(option)

        val spread = option.spread
        val angle = option.angle
        val radius = option.radius
        val step = option.step
        val slope = option.slope
        val start = option.startAngle
        val rotateAxisX = option.rotateAxisX
        val rotateAxisY = option.rotateAxisY
        val rotateAxisZ = option.rotateAxisZ

        option.createContainer(target, context).thenAccept { container ->
            container.forEachLocation {
                val builder = Builder(this, spread, angle, radius, step, slope, start,rotateAxisX, rotateAxisY, rotateAxisZ, spawner)
                ParticleFrame.create(option.period, builder, response)
            }

        }
    }

    class Builder(
        val location: Location,
        val spread: Double,
        val angle: Double,
        val radius: Double,
        val step: Double,
        val slope: Double,
        val start: Double,
        val rotateAxisX: Double = 0.0,
        val rotateAxisY: Double = 0.0,
        val rotateAxisZ: Double = 0.0,
        spawner: EffectSpawner
    ) : ParticleFrame.Builder(spawner) {


        val coordinate = PlayerFrontCoordinate(location)

        var i = 0.0

        override fun next(): Location? {

            if (if (angle <= -1) i > angle else i < angle) {
                val radians = Math.toRadians(i + start)
                val vector = Vector()
                vector.x = (radius + abs(i) / step * spread) * cos(radians)
                vector.z = (radius + abs(i) / step * spread) * sin(radians)
                vector.y = abs(i) / step * slope
                rotateAxisVector(rotateAxisX, rotateAxisY, rotateAxisZ, vector)
                if (angle <= -1) {
                    i -= step
                } else {
                    i += step
                }
                return coordinate.newLocation(vector.x, vector.y, vector.z)
            }
            return null
        }

    }

    fun rotateAxisVector(rotateAxisX: Double, rotateAxisY: Double, rotateAxisZ: Double, vector: Vector) {
        rotateAroundAxisZ(vector, rotateAxisZ)
        rotateAroundAxisX(vector, rotateAxisX)
        rotateAroundAxisY(vector, rotateAxisY)
    }

}
