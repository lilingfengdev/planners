package com.bh.planners.core.selector.bukkit.shape

import com.bh.planners.api.PlannersOption
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.util.createAwaitVoidFuture
import com.bh.planners.core.effect.util.isInSphere
import com.bh.planners.core.selector.Selector
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.adaptLocation
import taboolib.common.platform.function.platformLocation
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.random
import taboolib.module.effect.createSphere
import java.util.concurrent.CompletableFuture
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 选中根据原点来定义的范围实体
 * @range 10
 * @range 5 5 5 false 5
 */
object Range : Selector {

    override val names: Array<String>
        get() = arrayOf("range", "r")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val location = data.origin.getLocation()?.clone() ?: return CompletableFuture.completedFuture(null)

        val x = data.read<Double>(0, "0.0")
        val y = data.read<Double>(1, x.toString())
        val z = data.read<Double>(2, x.toString())

        val random = data.read<Boolean>(3, "false")
        val amount = data.read<Int>(4, "1")

        val show = data.read<Boolean>(5, "false")

        if (show && x == y && y == z) {

            var i = 0

            submitAsync(period = 2) {

                createSphere(adaptLocation(location), x, 100, 0) {
                    data.context.player?.spawnParticle(Particle.FLAME, platformLocation(it), 1, 0.0, 0.0, 0.0, 0.0, null)
                }.show()

                if (i == PlannersOption.showTime * 10) {
                    cancel()
                }

                i++

            }

        }

        return createAwaitVoidFuture {
            if (random) {
                repeat(amount) {
                    val newX = location.x + random(-x, x)
                    val newY = location.y + random(-y, y)
                    val newZ = location.z + random(-z, z)

                    val newLocation = Location(location.world, newX, newY, newZ)

                    data.container.add(newLocation.toTarget())
                }
            } else {
                if (x == y && y == z) {
                    val entities = location.world?.livingEntities ?: return@createAwaitVoidFuture
                    entities.forEach {
                        val offset = sqrt(it.width.pow(2) * 2)/2
                        val r = x + offset
                        if (it.eyeLocation.isInSphere(location, r)) {
                            data.container += it.toTarget()
                        }
                    }
                } else {
                    location.world?.getNearbyEntities(location, x, y, z)?.forEach {
                        if (it is LivingEntity) {
                            data.container += it.toTarget()
                        }
                    }
                }
            }
        }

    }

}
