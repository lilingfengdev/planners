package com.bh.planners.core.kether.game

import com.bh.planners.api.entity.ProxyEntity
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.containerOrSender
import com.bh.planners.core.kether.nextSelectorOrNull
import org.bukkit.util.Vector
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionLaunch(
    val x: ParsedAction<*>,
    val y: ParsedAction<*>,
    val z: ParsedAction<*>,
    val selector: ParsedAction<*>?,
) : ScriptAction<Void>() {

    fun execute(entity: ProxyEntity, x: Double, y: Double, z: Double) {
        val vector1 = entity.location.direction.setY(0).normalize()
        val vector2 = vector1.clone().crossProduct(Vector(0, 1, 0))
        vector1.multiply(Coerce.toDouble(x))
        vector1.add(vector2.multiply(Coerce.toDouble(z))).y = Coerce.toDouble(y)
        entity.velocity = vector1
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.run(x).double { x ->
            frame.run(y).double { y ->
                frame.run(z).double { z ->
                    frame.containerOrSender(selector).thenAccept {
                        it.forEachProxyEntity { execute(this, x, y, z) }
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    internal object Parser {

        /**
         * 为目标添加一个基于视角方向的向量 (即冲刺)
         * launch [x] [y] [z] [selector]
         * launch -2 0.5 0 they "@self"  -  使自己向后跳跃
         * launch 2 0.5 0 they "@self"   -  使自己向前跳跃
         */
        @KetherParser(["launch"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val x = it.nextParsedAction()
            val y = it.nextParsedAction()
            val z = it.nextParsedAction()
            ActionLaunch(x, y, z, it.nextSelectorOrNull())
        }
    }
}