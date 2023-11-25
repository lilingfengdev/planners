package com.bh.planners.core.selector.bukkit.shape

import com.bh.planners.api.PlannersOption
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.util.createAwaitVoidFuture
import com.bh.planners.core.selector.Selector
import com.bh.planners.util.entityAt
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector
import taboolib.common.platform.function.adaptLocation
import taboolib.common.platform.function.platformLocation
import taboolib.common.platform.function.submitAsync
import taboolib.module.effect.createLine
import java.util.concurrent.CompletableFuture

/**
 * 选中视角所看向的实体集群
 * step 最小距离 最大距离 show
 * @visual 5 10 false
 * @vi 5 10 false
 */
object Visual : Selector {
    override val names: Array<String>
        get() = arrayOf("visual", "vi")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val location = data.origin.getLocation()?.clone() ?: return CompletableFuture.completedFuture(null)

        val minRange = data.read<Double>(0, "0")
        val maxRange = data.read<Double>(1, "1")
        val show = data.read<Boolean>(2, "false")

        if (minRange >= maxRange) {
            error("visual: minRange >= maxRange")
        }

        if (show) {

            var i = 0

            submitAsync(period = 2) {

                createLine(
                    adaptLocation(location.clone().add(location.direction.clone().normalize().multiply(minRange))),
                    adaptLocation(location.clone().add(location.direction.clone().normalize().multiply(maxRange))),
                    0.5,
                    0
                ) {
                    data.context.player?.spawnParticle(Particle.FLAME, platformLocation(it), 1, 0.0, 0.0, 0.0, 0.0, null)
                }.show()

                if (i == PlannersOption.showTime * 10) {
                    cancel()
                }

                i++

            }

        }

        return createAwaitVoidFuture {

            fun getTargetLocation(start: Location, dir: Vector, minRange: Double, maxRange: Double): Set<LivingEntity> {
                var maxrange = minRange
                val list = mutableSetOf<LivingEntity>()

                start.add(dir.clone().multiply(minRange))

                while (maxrange in minRange..maxRange) {

                    list += start.entityAt()

                    start.add(dir)
                    maxrange++
                }

                return list
            }

            getTargetLocation(location, location.direction.normalize(), minRange, maxRange).map { it.toTarget() }.forEach {
                data.container += it
            }
        }
    }


}