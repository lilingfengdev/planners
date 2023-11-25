package com.bh.planners.core.selector.bukkit.shape

import com.bh.planners.api.PlannersOption.showTime
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.util.createAwaitVoidFuture
import com.bh.planners.core.effect.util.isPointInEntitySector
import com.bh.planners.core.selector.Selector
import org.bukkit.Particle
import taboolib.common.platform.function.adaptLocation
import taboolib.common.platform.function.platformLocation
import taboolib.common.platform.function.submitAsync
import taboolib.module.effect.createArc
import taboolib.module.effect.createLine
import java.util.concurrent.CompletableFuture
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * radius(半径) angle(角度) yaw(偏航角偏移) show
 * sector 2 60 0 true
 */
object Sector : Selector {

    override val names: Array<String>
        get() = arrayOf("sector", "!sector")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val location = data.origin.getLocation()?.clone() ?: return CompletableFuture.completedFuture(null)

        // 修正视角位置 相对 -0.5
        val direction = location.direction.clone().normalize().multiply(-0.5)
        location.add(direction)

        val radius = data.read<Double>(0, "1")
        val angle = data.read<Double>(1, "0")

        val yaw = data.read<Float>(2, "0.0")
        val show = data.read<Boolean>(3, "false")

        location.yaw += yaw

        if (show) {

            var i = 0

            submitAsync(period = 2) {

                val degrees = if (location.yaw < 0) {
                    location.yaw + 90 + 720
                } else {
                    location.yaw + 90 + 360
                }

                var start = degrees - angle/2

                if (start >= 360) {
                    start -= 360
                }

                val arc = createArc(adaptLocation(location), start, start + angle, radius, 5.0, 0) {
                    data.context.player?.spawnParticle(Particle.FLAME, platformLocation(it), 1, 0.0, 0.0, 0.0, 0.0, null)
                }

                val locations = arc.calculateLocations()

                if (locations.size >= 2) {
                    createLine(locations.first(), adaptLocation(location), 0.5, 0) {
                        data.context.player?.spawnParticle(Particle.FLAME, platformLocation(it), 1, 0.0, 0.0, 0.0, 0.0, null)
                    }.show()
                    createLine(locations.first(), adaptLocation(location), 0.5, 0) {
                        data.context.player?.spawnParticle(Particle.FLAME, platformLocation(it), 1, 0.0, 0.0, 0.0, 0.0, null)
                    }.show()
                    createLine(locations.last(), adaptLocation(location), 0.5, 0) {
                        data.context.player?.spawnParticle(Particle.FLAME, platformLocation(it), 1, 0.0, 0.0, 0.0, 0.0, null)
                    }.show()
                    arc.show()
                }

                if (i == showTime * 10) {
                    cancel()
                }

                i++

            }

        }

        return createAwaitVoidFuture {

            val entities = location.world?.livingEntities ?: return@createAwaitVoidFuture

            entities.forEach { entity ->

                val offset = sqrt(entity.width.pow(2) * 2)/2

                if (isPointInEntitySector(entity.eyeLocation, location, radius + offset, angle)) {

                    if (data.isNon) {

                        data.container.removeIf { t -> t == entity }

                    } else {

                        data.container += entity.toTarget()

                    }

                }

            }

        }
    }

}
