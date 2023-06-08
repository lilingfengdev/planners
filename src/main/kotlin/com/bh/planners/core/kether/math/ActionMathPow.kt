package com.bh.planners.core.kether.math

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.readAccept
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture
import kotlin.math.pow

class ActionMathPow(val action: ParsedAction<*>, val value2: ParsedAction<*>) : ScriptAction<Any>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Any> {
        val future = CompletableFuture<Any>()
        frame.readAccept<Double>(action) { v1 ->
            frame.readAccept<Double>(value2) { v2 ->
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
            ActionMathPow(it.nextParsedAction(), it.nextParsedAction())
        }
    }

}