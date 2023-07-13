package com.bh.planners.core.kether

import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionContain(val key: String, val text: String) : ScriptAction<Boolean>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(text.contains(key))
    }

    companion object {

        @KetherParser(["contain"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionContain(it.nextToken(), it.nextToken())
        }

    }

}