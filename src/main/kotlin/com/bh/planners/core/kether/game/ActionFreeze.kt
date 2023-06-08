package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.*
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionFreeze(val ticks: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        frame.readAccept<Int>(ticks) { ticks ->
            if (selector != null) {
                frame.execEntity(selector) { freezeTicks = ticks }
            } else {
                frame.bukkitPlayer()?.freezeTicks = ticks
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        /**
         * 使目标冻结
         * freeze [ticks] [selector]
         */
        @KetherParser(["freeze"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val power = it.nextParsedAction()
            ActionFreeze(power, it.nextSelectorOrNull())
        }
    }
}