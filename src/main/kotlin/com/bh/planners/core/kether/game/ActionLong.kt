package com.bh.planners.core.kether.game

import com.bh.planners.core.effect.safeDistance
import com.bh.planners.core.kether.*
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionLong(
    val pos1: ParsedAction<*>,
    val pos2: ParsedAction<*>?,
) : ScriptAction<Double>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Double> {
        val future = CompletableFuture<Double>()
            frame.createContainer(pos1).thenAccept { pos1 ->
                frame.containerOrSender(pos2).thenAccept { pos2 ->
                    val loc1 = pos1.firstLocation() ?: pos1.firstProxyEntity()?.location ?: return@thenAccept
                    val loc2 = pos2.firstLocation() ?: pos2.firstProxyEntity()?.location ?: return@thenAccept
                    future.complete(loc1.safeDistance(loc2))
                }
            }
        return future
    }

    companion object {

        /**
         * long selector1 selector2(1)
         * 之间的距离
         */
        @KetherParser(["long"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionLong(it.nextParsedAction(), it.nextSelectorOrNull())
        }

    }


}