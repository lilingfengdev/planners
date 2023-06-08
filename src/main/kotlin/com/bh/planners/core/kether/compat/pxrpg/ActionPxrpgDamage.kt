package com.bh.planners.core.kether.compat.pxrpg

import com.bh.planners.api.event.compat.PxrpxEvents
import com.bh.planners.core.kether.*
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionPxrpgMark(val id: ParsedAction<*>, val selector: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val player = frame.bukkitPlayer() ?: return CompletableFuture.completedFuture(null)

        val future = CompletableFuture<Void>()

        frame.run(id).str { id ->
            frame.container(selector).thenAccept {
                it.forEachLivingEntity {
                    PxrpxEvents.Mark(id, frame.skill(), player, this).call()
                    future.complete(null)
                }
            }
        }

        return future
    }

    companion object {

        /**
         * 对selector目标造成伤害
         * px damage [damage] [selector]
         * px damage 10.0 they ":@aline 10"
         */
        @KetherParser(["px", "pxrpg"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionPxrpgMark(it.nextParsedAction(), it.nextSelector())
        }

    }

}