package com.bh.planners.core.kether.math

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.readAccept
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture
import kotlin.math.cos
import kotlin.math.sin

class ActionMathCos(val action: ParsedAction<*>) : ScriptAction<Any>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Any> {
        val future = CompletableFuture<Any>()
        frame.readAccept<Double>(action) {
            future.complete(cos(it))
        }
        return future
    }

    companion object {

        /**
         * cos [value: action]
         */
        @KetherParser(["cos"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionMathCos(it.nextParsedAction())
        }
    }

}