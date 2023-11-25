package com.bh.planners.core.selector.bukkit.shape

import com.bh.planners.api.PlannersOption
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.util.createAwaitVoidFuture
import com.bh.planners.core.effect.util.isPointInsideCuboid
import com.bh.planners.core.selector.Selector
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.util.Vector
import taboolib.common.platform.function.adaptLocation
import taboolib.common.platform.function.platformLocation
import taboolib.common.platform.function.submitAsync
import taboolib.module.effect.createLine
import java.util.concurrent.CompletableFuture

/**
 * 视角前长方形
 * Long 长
 * wide 宽
 * high 高
 * forward 前后偏移
 * offsetY 上下偏移
 * pitch 是否根据pitch改变长方形方向
 *
 * @rectangle Long wide high forward offsetY pitch
 */
object Rectangle : Selector {

    override val names: Array<String>
        get() = arrayOf("rectangle", "rec")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val location = data.origin.getLocation()?.clone() ?: return CompletableFuture.completedFuture(null)

        val long = data.read<Double>(0, "0.0")
        val wide = data.read<Double>(1, "0.0")
        val high = data.read<Double>(2, "0.0")
        val forward = data.read<Double>(3, "0.0")
        val offsetY = data.read<Double>(4, "0.0")
        val pitch = data.read<Boolean>(5, "false")
        val show = data.read<Boolean>(6, "false")

        return createAwaitVoidFuture {

            val entities = location.world?.livingEntities ?: return@createAwaitVoidFuture

            if (!pitch) {
                location.pitch = 0f
            }

            val vectorX1 = location.clone().direction.normalize()
            val vectorY1 =
                if (location.yaw in -360.0..-180.0 || location.yaw in 0.0..180.0) {
                    vectorX1.clone().setZ(0).crossProduct(Vector(0, 0, 1)).normalize()
                } else {
                    vectorX1.clone().setZ(0).crossProduct(Vector(0, 0, -1)).normalize()
                }
            val vectorZ1 = vectorX1.clone().crossProduct(vectorY1.clone()).normalize()

            val locA = location.clone().add(vectorX1.clone().multiply(forward)).add(vectorY1.clone().multiply(offsetY)).add(vectorZ1.clone().multiply(-(wide / 2)))
            val locB = location.clone().add(vectorX1.clone().multiply(forward)).add(vectorY1.clone().multiply(offsetY)).add(vectorZ1.clone().multiply(wide / 2))

            val locC = location.clone().add(vectorX1.clone().multiply(forward)).add(vectorY1.clone().multiply(offsetY + high)).add(vectorZ1.clone().multiply(-(wide / 2)))
            val locD = location.clone().add(vectorX1.clone().multiply(forward)).add(vectorY1.clone().multiply(offsetY + high)).add(vectorZ1.clone().multiply(wide / 2))

            val locAF = locA.clone().add(vectorX1.clone().multiply(long))
            val locBF = locB.clone().add(vectorX1.clone().multiply(long))
            val locCF = locC.clone().add(vectorX1.clone().multiply(long))
            val locDF = locD.clone().add(vectorX1.clone().multiply(long))

            if (show) {

                var i = 0

                submitAsync(period = 2) {

                    i++

                    fun spawn(loc1: Location, loc2: Location) {
                        createLine(adaptLocation(loc1), adaptLocation(loc2), 0.5, 0) {
                            data.context.player?.spawnParticle(Particle.FLAME, platformLocation(it), 1, 0.0, 0.0, 0.0, 0.0, null)
                        }.show()
                    }

                    spawn(locA, locAF)
                    spawn(locB, locBF)
                    spawn(locC, locCF)
                    spawn(locD, locDF)

                    spawn(locA, locB)
                    spawn(locA, locC)
                    spawn(locB, locD)
                    spawn(locC, locD)

                    spawn(locAF, locBF)
                    spawn(locAF, locCF)
                    spawn(locBF, locDF)
                    spawn(locCF, locDF)

                    if (i >= PlannersOption.showTime * 10) {
                        cancel()
                    }

                }

            }

            val array = arrayOf(locA, locB, locC, locD, locAF, locBF, locCF, locDF)

            entities.forEach {

                if (isPointInsideCuboid(it.location, array)) {
                    data.container += it.toTarget()
                }

            }
        }

    }

}