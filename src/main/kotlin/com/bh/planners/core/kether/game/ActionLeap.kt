package com.bh.planners.core.kether.game

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper
import com.bh.planners.core.kether.common.KetherHelper.containerOrOrigin
import com.bh.planners.core.kether.getContext
import com.bh.planners.core.kether.parseTargetContainer
import org.bukkit.Location
import org.bukkit.util.Vector

@CombinationKetherParser.Used
fun leap() = KetherHelper.simpleKetherParser<Unit>("leap") {
    it.group(double(), any(), containerOrOrigin()).apply(it) { step, target, container ->
        now {
            val pos = container.firstBukkitLocation()!!
            parseTargetContainer(target ?: Target.Container(),getContext()).forEachProxyEntity {
                velocity = velocity.add(next(location, pos, step))
            }
        }
    }
}

private fun next(locA: Location, locB: Location, step: Double): Vector {
    val vectorAB = locB.clone().subtract(locA).toVector()
    vectorAB.normalize()
    vectorAB.multiply(step)
    return vectorAB
}