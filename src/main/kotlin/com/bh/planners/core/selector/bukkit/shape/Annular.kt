package com.bh.planners.core.selector.bukkit.shape

import com.bh.planners.api.PlannersOption
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.util.createAwaitVoidFuture
import com.bh.planners.core.effect.util.isInRound
import com.bh.planners.core.selector.Selector
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.util.Vector
import taboolib.common.platform.function.adaptLocation
import taboolib.common.platform.function.platformLocation
import taboolib.common.platform.function.submitAsync
import taboolib.module.effect.createCircle
import java.util.concurrent.CompletableFuture
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 选中根据原点来定义的环状实体
 * @annular 2 3 5 false
 * @annular min max high show
 *
 */
object Annular : Selector {

    override val names: Array<String>
        get() = arrayOf("annular")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val location = data.origin.getLocation()?.clone() ?: return CompletableFuture.completedFuture(null)

        val min = data.read<Double>(0, "0.0")
        val max = data.read<Double>(1, "0.0")
        val high = data.read<Double>(2, "0.0")
        val show = data.read<Boolean>(3, "false")

        if (show) {
            var i = 0

            submitAsync(period = 2) {

                fun circleMax(loc: Location) {

                    createCircle(
                        adaptLocation(loc),
                        max,
                        5.0,
                        0
                    ) {
                        data.context.player?.spawnParticle(Particle.FLAME, platformLocation(it), 1, 0.0, 0.0, 0.0, 0.0, null)
                    }.show()

                }

                fun circleMin(loc: Location) {

                    createCircle(
                        adaptLocation(loc),
                        min,
                        5.0,
                        0
                    ) {
                        data.context.player?.spawnParticle(Particle.FLAME, platformLocation(it), 1, 0.0, 0.0, 0.0, 0.0, null)
                    }.show()

                }

                circleMax(location.clone().add(Vector(0.0, high / 2, 0.0)))
                circleMax(location.clone())
                circleMax(location.clone().add(Vector(0.0, -high / 2, 0.0)))

                circleMin(location.clone().add(Vector(0.0, high / 2, 0.0)))
                circleMin(location.clone())
                circleMin(location.clone().add(Vector(0.0, -high / 2, 0.0)))

                if (i == PlannersOption.showTime * 10) {
                    cancel()
                }

                i++

            }
        }

        return createAwaitVoidFuture {
            val livingEntities = location.world?.livingEntities ?: return@createAwaitVoidFuture
            livingEntities.forEach {
                val offset = sqrt(it.width.pow(2) * 2) / 2
                if (it.location.isInRound(location, (max + offset).coerceAtLeast(min)) && !it.eyeLocation.isInRound(location, (min - offset).coerceAtLeast(0.0).coerceAtMost(max)) && location.y in (location.y - high/2)..(location.y + high/2)) {
                    data.container += it.toTarget()
                }
            }
        }

    }

}
