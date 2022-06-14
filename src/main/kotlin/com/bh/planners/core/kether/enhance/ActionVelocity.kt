package com.bh.planners.core.kether.enhance

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.createTargets
import org.bukkit.util.Vector
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionVelocity(
    val mode: Mode,
    val x: ParsedAction<*>,
    val y: ParsedAction<*>,
    val z: ParsedAction<*>,
    val selector: ParsedAction<*>
): ScriptAction<Any>() {

    enum class Mode {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, SET
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Any> {
        val vector = CompletableFuture<Any>()
        frame.newFrame(x).run<Any>().thenApply { x ->
            frame.newFrame(y).run<Any>().thenApply { y ->
                frame.newFrame(z).run<Any>().thenApply { z ->
                    frame.createTargets(selector).thenApply { container ->
                        container.forEachEntity {
                            var vec = this.velocity
                            when(mode) {
                                Mode.ADD -> {
                                    vec.x = vec.x + Coerce.toDouble(x)
                                    vec.y = vec.y + Coerce.toDouble(y)
                                    vec.z = vec.z + Coerce.toDouble(z)
                                }
                                Mode.SUBTRACT -> {
                                    vec.x = vec.x - Coerce.toDouble(x)
                                    vec.y = vec.y - Coerce.toDouble(y)
                                    vec.z = vec.z - Coerce.toDouble(z)
                                }
                                Mode.MULTIPLY -> {
                                    vec.x = vec.x * Coerce.toDouble(x)
                                    vec.y = vec.y * Coerce.toDouble(y)
                                    vec.z = vec.z * Coerce.toDouble(z)
                                }
                                Mode.DIVIDE -> {
                                    vec.x = vec.x / Coerce.toDouble(x)
                                    vec.y = vec.y / Coerce.toDouble(y)
                                    vec.z = vec.z / Coerce.toDouble(z)
                                }
                                Mode.SET -> {
                                    vec = Vector(Coerce.toDouble(x), Coerce.toDouble(y), Coerce.toDouble(z))
                                }
                            }
                            this.velocity = vec
                            vector.complete(vec)
                        }
                    }
                }
            }
        }
        return vector
    }

    internal object Parser {

        @KetherParser(["velocity"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            val mode = when (val mode = it.nextToken().lowercase(Locale.getDefault())) {
                    "add" -> Mode.ADD
                    "subtract", "sub", "minus" -> Mode.SUBTRACT
                    "multiply", "mul" -> Mode.MULTIPLY
                    "divide", "div" -> Mode.DIVIDE
                    "set" -> Mode.SET
                    else -> throw KetherError.CUSTOM.create(mode)
                }
            val x = it.next(ArgTypes.ACTION)
            val y = it.next(ArgTypes.ACTION)
            val z = it.next(ArgTypes.ACTION)
            ActionVelocity(mode, x, y, z, it.next(ArgTypes.ACTION))
        }
    }
}