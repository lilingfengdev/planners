package com.bh.planners.core.kether

import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestAction
import taboolib.library.kether.QuestContext
import taboolib.library.kether.actions.LiteralAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionSet(val action: ParsedAction<*>, val value: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        frame.newFrame(action).run<Any>().thenAccept { key ->
            frame.newFrame(value).run<Any>().thenAccept {
                frame.rootVariables()[key.toString()] = it
                future.complete(null)
            }
        }
        return future
    }

    internal object Parser {

        /**
         * set xx to xx
         * set property xx from xx to xx
         */
        @KetherParser(["set"], namespace = NAMESPACE, shared = true)
        fun parser0() = scriptParser {
            val key = it.next(ArgTypes.ACTION)
            val value = try {
                it.mark()
                it.expects("to", "set")
                it.next(ArgTypes.ACTION)
            } catch (_: Exception) {
                it.reset()
                it.next(ArgTypes.ACTION)
            }
            ActionSet(key, value)
        }
    }

}