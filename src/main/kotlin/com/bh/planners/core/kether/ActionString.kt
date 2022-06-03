package com.bh.planners.core.kether

import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionString {

    class StringContain(val action: ParsedAction<*>, val value: ParsedAction<*>) : ScriptAction<Boolean>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {

            val future = CompletableFuture<Boolean>()

            frame.newFrame(action).run<String>().thenAccept { action ->
                frame.newFrame(value).run<String>().thenAccept { value ->
                    future.complete(action.contains(value))
                }
            }
            return future
        }

    }

    companion object {

        @KetherParser(["contain"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            StringContain(it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION))
        }

    }

}