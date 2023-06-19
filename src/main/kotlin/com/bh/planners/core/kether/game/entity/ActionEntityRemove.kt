package com.bh.planners.core.kether.game.entity

import com.bh.planners.core.kether.containerOrSender
import org.bukkit.entity.Entity
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionEntityRemove(
    val selector: ParsedAction<*>,
) : ScriptAction<List<Entity>>() {

    override fun run(frame: ScriptFrame): CompletableFuture<List<Entity>> {
        frame.containerOrSender(selector).thenAccept {
            it.forEachEntity {
                this.remove()
            }
        }
        return CompletableFuture.completedFuture(null)
    }

}