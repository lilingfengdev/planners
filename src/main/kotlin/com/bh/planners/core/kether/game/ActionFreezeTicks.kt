package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.*
import org.bukkit.Location
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionFreezeTicks(val ticks: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        frame.runTransfer0<Int>(ticks) { ticks ->
            if (selector != null) {
                frame.execEntity(selector) { freezeTicks = ticks }
            } else {
                frame.asPlayer()?.freezeTicks = ticks
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        /**
         * 使目标冻结
         * explosion [ticks] [selector]
         */
        @KetherParser(["freezeTicks"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val power = it.next(ArgTypes.ACTION)
            ActionFreezeTicks(power, it.selectorAction())
        }
    }
}