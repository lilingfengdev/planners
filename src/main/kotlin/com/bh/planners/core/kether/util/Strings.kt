package com.bh.planners.core.kether.util

import com.bh.planners.core.kether.NAMESPACE
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class Strings {

    class Contain(val action: ParsedAction<*>, val value: ParsedAction<*>) : ScriptAction<Boolean>() {

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

        @KetherParser(["contain"], namespace = NAMESPACE, shared = true)
        fun parser1() = scriptParser {
            Contain(it.nextParsedAction(), it.nextParsedAction())
        }


    }

}
