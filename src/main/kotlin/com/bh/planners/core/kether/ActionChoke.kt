package com.bh.planners.core.kether

import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionChoke(val value: ParsedAction<*>) : ScriptAction<Void>() {

    /**
     * 不推荐
     */
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        frame.read<Long>(value).thenAccept {
            Thread.sleep(it)
            future.complete(null)
        }
        return future
    }

    companion object {

        @KetherParser(["choke"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionChoke(it.nextParsedAction())
        }

    }

}