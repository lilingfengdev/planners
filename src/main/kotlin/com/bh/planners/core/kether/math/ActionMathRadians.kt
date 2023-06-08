package com.bh.planners.core.kether.math

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.readAccept
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionMathRadians(val action: ParsedAction<*>) : ScriptAction<Any>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Any> {
        val future = CompletableFuture<Any>()
        frame.readAccept<Double>(action) {
            future.complete(Math.toRadians(it))
        }
        return future
    }

    companion object {

        /**
         * cos [value: action]
         */
        @KetherParser(["radians"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionMathRadians(it.nextParsedAction())
        }
    }

}