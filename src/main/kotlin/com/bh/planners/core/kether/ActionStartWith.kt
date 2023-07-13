package com.bh.planners.core.kether

import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionStartWith(val key: String, val text: String) : ScriptAction<Boolean>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(text.startsWith(key))
    }

    companion object {

        @KetherParser(["startwith"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionStartWith(it.nextToken(), it.nextToken())
        }

    }

}