package com.bh.planners.core.kether.game.entity

import com.bh.planners.core.kether.createContainer
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionEntityFieldGet(val field: EntityField, val selector: ParsedAction<*>) : ScriptAction<Any>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Any> {
        val future = CompletableFuture<Any>()

        frame.createContainer(selector).thenAccept {
            val entityTarget = it.firstProxyEntity(bukkit = false)
            if (entityTarget != null) {
                future.complete(field.get(entityTarget))
            } else future.complete(null)
        }

        return future
    }
}