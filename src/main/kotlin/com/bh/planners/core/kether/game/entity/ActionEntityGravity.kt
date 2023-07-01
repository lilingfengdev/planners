package com.bh.planners.core.kether.game.entity

import com.bh.planners.core.kether.container
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.bool
import taboolib.module.kether.run
import java.util.concurrent.CompletableFuture

class ActionEntityGravity(
    val gravity: ParsedAction<*>,
    val selector: ParsedAction<*>,
) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.run(gravity).bool { gravity ->
            frame.container(selector).thenAccept {
                it.forEachEntity {
                    setGravity(gravity)
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

}