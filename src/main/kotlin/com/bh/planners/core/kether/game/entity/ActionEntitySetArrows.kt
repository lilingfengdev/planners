package com.bh.planners.core.kether.game.entity

import com.bh.planners.core.kether.createContainer
import org.bukkit.entity.Entity
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.int
import taboolib.module.kether.run
import java.util.concurrent.CompletableFuture

class ActionEntitySetArrows(
    val number: ParsedAction<*>,
    val selector: ParsedAction<*>,
) : ScriptAction<List<Entity>>() {

    override fun run(frame: ScriptFrame): CompletableFuture<List<Entity>> {
        frame.run(number).int { arrows ->
            frame.createContainer(selector).thenAccept { container ->
                container.forEachLivingEntity {
                    this.arrowsInBody = arrows
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

}