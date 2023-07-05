package com.bh.planners.core.kether.compat.dragoncore

import com.bh.planners.core.kether.containerOrSender
import com.bh.planners.core.kether.readAccept
import com.qq410120288.minecraft.antikey.main.StopMove
import com.qq410120288.minecraft.antikey.main.StopMoveEnd
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionDragonStopMove(
    val duration: ParsedAction<*>,
    val selector: ParsedAction<*>?,
) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        frame.readAccept<Long>(duration) { duration ->
            frame.containerOrSender(selector).thenAccept { container ->
                container.forEachPlayer {
                    if (duration.toInt() == 0) {
                        StopMoveEnd(this)
                    } else {
                        StopMove(this, duration * 50)
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }
}