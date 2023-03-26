package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.*
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.module.navigation.set
import java.util.concurrent.CompletableFuture

class ActionVelocity(
    val mode: Mode,
    val x: ParsedAction<*>,
    val y: ParsedAction<*>,
    val z: ParsedAction<*>,
    val selector: ParsedAction<*>?
) : ScriptAction<Void>() {

    enum class Mode {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, SET
    }

    fun execute(entity: Entity, mode: Mode, vector: Vector) {
        val vec = entity.velocity
        when (mode) {
            Mode.ADD -> vec.add(vector)
            Mode.SUBTRACT -> vec.subtract(vector)
            Mode.MULTIPLY -> vec.multiply(vector)
            Mode.DIVIDE -> vec.divide(vector)
            Mode.SET -> vec.set(vector.x, vector.y, vector.z)
        }
        entity.velocity = vec
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(x).run<Any>().thenApply { x ->
            frame.newFrame(y).run<Any>().thenApply { y ->
                frame.newFrame(z).run<Any>().thenApply { z ->
                    val toVector = Vector(Coerce.toDouble(x), Coerce.toDouble(y), Coerce.toDouble(z))
                    if (selector != null) {
                        frame.execEntity(selector) { execute(this, mode, toVector) }
                    } else {
                        execute(frame.bukkitPlayer()!!, mode, toVector)
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    internal object Parser {

        /**
         * 为目标设置 "固定" 向量 (不跟随视角方向)
         * velocity [mode] [x] [y] [z] [selector]
         * velocity add 1 1 0 "@self"
         */
        @KetherParser(["velocity"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val mode = when (it.expects("add", "subtract", "sub", "minus", "multiply", "mul", "div", "divide", "set")) {
                "add" -> Mode.ADD
                "subtract", "sub", "minus" -> Mode.SUBTRACT
                "multiply", "mul" -> Mode.MULTIPLY
                "divide", "div" -> Mode.DIVIDE
                "set" -> Mode.SET
                else -> error("error")
            }
            val x = it.nextParsedAction()
            val y = it.nextParsedAction()
            val z = it.nextParsedAction()
            ActionVelocity(mode, x, y, z, it.nextSelectorOrNull())
        }
    }
}