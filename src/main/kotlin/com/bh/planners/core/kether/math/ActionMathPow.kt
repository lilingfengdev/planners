package com.bh.planners.core.kether.math

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.runTransfer0
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture
import kotlin.math.pow
import kotlin.math.sin

class ActionMathPow(val action: ParsedAction<*>, val value2: ParsedAction<*>) : ScriptAction<Any>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Any> {
        val future = CompletableFuture<Any>()
        frame.runTransfer0<Double>(action) { v1 ->
            frame.runTransfer0<Double>(value2) { v2 ->
                future.complete(v1.pow(v2))
            }
        }
        return future
    }

    companion object {

        /**
         * pow [value: action] [pow: action]
         */
        @KetherParser(["pow"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionMathPow(it.next(ArgTypes.ACTION),it.next(ArgTypes.ACTION))
        }
    }

}