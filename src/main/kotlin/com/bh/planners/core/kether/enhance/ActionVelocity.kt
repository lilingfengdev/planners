package com.bh.planners.core.kether.enhance

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.createTargets
import org.bukkit.util.Vector
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.module.navigation.set
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionVelocity(
    val mode: Mode,
    val x: ParsedAction<*>,
    val y: ParsedAction<*>,
    val z: ParsedAction<*>,
    val selector: ParsedAction<*>
) : ScriptAction<Void>() {

    enum class Mode {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, SET
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(x).run<Any>().thenApply { x ->
            frame.newFrame(y).run<Any>().thenApply { y ->
                frame.newFrame(z).run<Any>().thenApply { z ->
                    frame.createTargets(selector).thenApply { container ->
                        val toVector = Vector(Coerce.toDouble(x), Coerce.toDouble(y), Coerce.toDouble(z))
                        container.forEachEntity {
                            val vec = this.velocity
                            when (mode) {
                                Mode.ADD -> vec.add(toVector)
                                Mode.SUBTRACT -> vec.subtract(toVector)
                                Mode.MULTIPLY -> vec.multiply(toVector)
                                Mode.DIVIDE -> vec.divide(toVector)
                                Mode.SET -> vec.set(toVector.x, toVector.y, toVector.z)
                            }
                            this.velocity = vec
                        }
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    internal object Parser {

        @KetherParser(["velocity"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            val mode = when (it.expects("add", "subtract", "sub", "minus", "multiply", "mul", "div", "divide", "set")) {
                "add" -> Mode.ADD
                "subtract", "sub", "minus" -> Mode.SUBTRACT
                "multiply", "mul" -> Mode.MULTIPLY
                "divide", "div" -> Mode.DIVIDE
                "set" -> Mode.SET
                else -> error("error")
            }
            val x = it.next(ArgTypes.ACTION)
            val y = it.next(ArgTypes.ACTION)
            val z = it.next(ArgTypes.ACTION)
            ActionVelocity(mode, x, y, z, it.next(ArgTypes.ACTION))
        }
    }
}