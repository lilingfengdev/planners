package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.nextSelector
import com.bh.planners.core.kether.origin
import org.bukkit.Location
import org.bukkit.util.Vector
import taboolib.common.util.randomDouble
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture
import kotlin.math.*

class ActionPush(
    val step: ParsedAction<*>,
    val selector: ParsedAction<*>,
    val pos: ParsedAction<*>?,
) : ScriptAction<Void>() {

    fun calculateLaunchAngle(from: Location, to: Location?, v: Double, elevation: Double, g: Double): Double? {
        val vector = from.clone().subtract(to!!).toVector()
        val distance = sqrt(vector.x.pow(2.0) + vector.z.pow(2.0))
        val v2 = v.pow(2.0)
        val v4 = v.pow(4.0)
        val check = g * (g * distance.pow(2.0) + 2.0 * elevation * v2)
        return if (v4 < check) null else atan((v2 - sqrt(v4 - check)) / (g * distance))
    }

    fun calculateHangtime(launchAngle: Double, v: Double, elev: Double, g: Double): Double {
        val a = v * sin(launchAngle)
        val b = -2.0 * g * elev
        return if (a.pow(2.0) + b < 0.0) 0.0 else (a + sqrt(a.pow(2.0) + b)) / g
    }

    fun normalizeVector(victor: Vector): Vector {
        val mag = sqrt(victor.x.pow(2.0) + victor.y.pow(2.0) + victor.z.pow(2.0))
        return if (mag != 0.0) victor.multiply(1.0 / mag) else victor.multiply(0)
    }

    fun execute(am: Location, location: Location, power: Double): Vector? {
        var velocity = 0.0
        velocity *= (1.0 + power.toDouble() * 0.1)
        if (am.world == location.world) {
            var v = location.clone().subtract(am).toVector()
            val elevation = v.y
            var launchAngle = calculateLaunchAngle(am, location, velocity, elevation, 20.0)
            val distance = sqrt(v.x.pow(2.0) + v.z.pow(2.0))
            if (distance != 0.0) {
                if (launchAngle == null) {
                    launchAngle = atan(
                        (40.0 * elevation + velocity.pow(2.0)) / (40.0 * elevation + 2.0 * velocity.pow(2.0))
                    )
                }
                val hangtime = calculateHangtime(launchAngle, velocity, elevation, 20.0)
                v.setY(tan(launchAngle) * distance)
                v = normalizeVector(v)
                var noise = Vector.getRandom()
                noise = noise.multiply(noise.multiply(0.1))
                v.add(noise)
                velocity += 1.188 * hangtime.pow(2.0) + (randomDouble() - 0.8) / 2.0
                v = v.multiply(velocity / 20.0)
                if (v.length() > 4.0) {
                    v = v.normalize().multiply(4)
                }
                if (java.lang.Double.isNaN(v.x)) {
                    v.setX(0)
                }
                if (java.lang.Double.isNaN(v.y)) {
                    v.setY(0)
                }
                if (java.lang.Double.isNaN(v.z)) {
                    v.setZ(0)
                }
                return v
            }
        }
        return null
    }

    companion object {

        /**
         * push step selector1 selector2(1)
         */
        @KetherParser(["push"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionDrag(it.nextParsedAction(), it.nextParsedAction(), it.nextSelector())
        }

    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.run(step).double { step ->
            frame.createContainer(selector).thenAccept { container ->
                if (pos != null) {
                    frame.createContainer(pos).thenAccept {
                        val pos = it.firstLocation() ?: error("ActionDrag 'pos' empty")
                        container.forEachProxyEntity {
                            execute(this.location, pos, step)?.let { v -> this.velocity = v }
                        }
                    }
                } else {
                    container.forEachProxyEntity {
                        execute(this.location, frame.origin().value, step)?.let { v -> this.velocity = v }
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }
}

