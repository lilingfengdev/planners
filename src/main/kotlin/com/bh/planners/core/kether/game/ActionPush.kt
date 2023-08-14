package com.bh.planners.core.kether.game

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.*
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper.containerOrEmpty
import com.bh.planners.core.kether.common.SimpleKetherParser
import org.bukkit.Location
import org.bukkit.util.Vector
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

@CombinationKetherParser.Used
object ActionPush : SimpleKetherParser("push"){

    override fun run(): ScriptActionParser<out Any?> {
        return combinationParser {
            it.group(double(), any(),containerOrEmpty()).apply(it) { step,selector,pos ->
                now {
                    val container = parseTargetContainer(selector ?: Target.Container(), getContext())
                    val location = pos.firstBukkitLocation()!!
                    container.forEachProxyEntity {
                        execute(this.location,location,step)
                    }
                }
            }
        }
    }

    private fun execute(locA: Location, locB: Location, step: Double): Vector {
        val vectorAB = locB.clone().subtract(locA).toVector()
        vectorAB.normalize()
        vectorAB.multiply(step)
        vectorAB.y = 0.0
        return vectorAB
    }
}