package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.nextSelector
import com.bh.planners.core.kether.origin
import org.bukkit.Location
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture
import kotlin.math.tan

class ActionIsforward(
        val selector: ParsedAction<*>,
        val pos: ParsedAction<*>?,
) : ScriptAction<Boolean>() {

    fun isforward(loc1: Location, loc2: Location?): Boolean {
        val locA = loc1.clone()
        val locB = loc2?.clone() ?: return false
        val yaw1 = locA.yaw.toDouble()
        val yawA: Double = if (yaw1 > 180.0) {
            yaw1 - 360.0
        } else if (yaw1 < -180.0) {
            yaw1 + 360.0
        } else {
            yaw1
        }
        return if (yawA > 90 || yawA < -90) {
            tan(Math.toRadians(yawA)) * locB.x + locA.z - tan(Math.toRadians(yawA)) * locA.x > locB.z
        } else {
            tan(Math.toRadians(yawA)) * locB.x + locA.z - tan(Math.toRadians(yawA)) * locA.x < locB.z
        }
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        frame.createContainer(selector).thenAccept { container ->
            if (pos != null) {
                frame.createContainer(pos).thenAccept {
                    val pos = it.firstLocation() ?: error("ActionDrag 'pos' empty")
                    container.forEachProxyEntity {
                        future.complete(isforward(pos, this.location))
                    }
                }
            } else {
                container.forEachProxyEntity {
                    future.complete(isforward(frame.origin().value, this.location))
                }
            }
        }
        return future
    }

    companion object {
        /**
         * isforward selector1 selector2(1)
         */
        @KetherParser(["isforward"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionIsforward(it.nextParsedAction(), it.nextSelector())
        }

    }

}