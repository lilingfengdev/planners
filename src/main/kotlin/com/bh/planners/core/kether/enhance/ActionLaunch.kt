package com.bh.planners.core.kether.enhance

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.asPlayer
import com.bh.planners.core.kether.createTargets
import com.bh.planners.core.kether.selectorAction
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionLaunch(
    val x: ParsedAction<*>,
    val y: ParsedAction<*>,
    val z: ParsedAction<*>,
    val selector: ParsedAction<*>?
) : ScriptAction<Void>() {

    fun execute(livingEntity: LivingEntity, x: Double, y: Double, z: Double) {
        val vector1 = livingEntity.location.direction.setY(0).normalize()
        val vector2 = vector1.clone().crossProduct(Vector(0, 1, 0))
        vector1.multiply(Coerce.toDouble(x))
        vector1.add(vector2.multiply(Coerce.toDouble(z))).y = Coerce.toDouble(y)
        livingEntity.velocity = vector1
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(x).run<Any>().thenApply {
            val x = Coerce.toDouble(it)
            frame.newFrame(y).run<Any>().thenApply {
                val y = Coerce.toDouble(it)
                frame.newFrame(z).run<Any>().thenApply {
                    val z = Coerce.toDouble(it)
                    if (selector != null) {
                        frame.createTargets(selector).thenApply { container ->
                            container.forEachEntity { execute(this, x, y, z) }
                        }
                    } else {
                        execute(frame.asPlayer()!!, x, y, z)
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
         * launch -2 0.5 0 "-@self"  -  使自己向后跳跃
         * launch 2 0.5 0 "-@self"   -  使自己向前跳跃
         */
        @KetherParser(["launch"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            val x = it.next(ArgTypes.ACTION)
            val y = it.next(ArgTypes.ACTION)
            val z = it.next(ArgTypes.ACTION)
            ActionLaunch(x, y, z, it.selectorAction())
        }
    }
}