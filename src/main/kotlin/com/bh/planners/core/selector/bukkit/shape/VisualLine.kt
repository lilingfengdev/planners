package com.bh.planners.core.selector.bukkit.shape

import com.bh.planners.api.PlannersOption
import com.bh.planners.core.effect.Target.Companion.getEntity
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.util.createAwaitVoidFuture
import com.bh.planners.core.selector.Selector
import org.bukkit.Particle
import taboolib.common.platform.function.adaptLocation
import taboolib.common.platform.function.platformLocation
import taboolib.common.platform.function.submitAsync
import taboolib.module.effect.createSphere
import java.util.concurrent.CompletableFuture
import kotlin.math.ceil

/**
 * length 距离
 * radius 半径
 */
object VisualLine : Selector {

    override val names: Array<String>
        get() = arrayOf("v-line", "!v-line")

    override fun check(data: Selector.Data): CompletableFuture<Void> {

        val location = data.origin.getLocation()?.clone() ?: return CompletableFuture.completedFuture(null)

        val direction = location.direction

        val length = data.read<Double>(0, "5")
        val radius = data.read<Double>(1, "1")
        val show = data.read<Boolean>(2, "false")

        val newLocation = location.add(direction.multiply(radius))

        if (show) {

            var i = 0

            submitAsync(period = 2) {

                createSphere(adaptLocation(newLocation), radius, 50, 0) {
                    data.context.player?.spawnParticle(Particle.FLAME, platformLocation(it), 1, 0.0, 0.0, 0.0, 0.0, null)
                }.show()

                if (i == PlannersOption.showTime * 10) {
                    cancel()
                }

                i++

            }

        }


        val amount = ceil(length / radius)


        return createAwaitVoidFuture {
            repeat(amount.toInt()) {
                location.world?.getNearbyEntities(newLocation, radius, radius, radius)
                    ?.forEach {
                        if (data.isNon) {
                            data.container.removeIf { it.getEntity() == it }
                        } else {
                            data.container += it.location.toTarget()
                        }
                    }

            }
        }
    }
}