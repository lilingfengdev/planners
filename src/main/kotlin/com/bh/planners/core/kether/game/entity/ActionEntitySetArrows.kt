package com.bh.planners.core.kether.game.entity

import com.bh.planners.core.kether.createContainer
import org.bukkit.entity.Entity
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionEntitySetArrows(
        val stats: ParsedAction<*>,
        val number: ParsedAction<*>,
        val selector: ParsedAction<*>,
) : ScriptAction<List<Entity>>() {

    override fun run(frame: ScriptFrame): CompletableFuture<List<Entity>> {
        frame.run(stats).str { stats ->
            frame.run(number).int { arrows ->
                frame.createContainer(selector).thenAccept { container ->
                    container.forEachLivingEntity {
                        when (stats) {
                            "add" -> {
                                this.arrowsInBody += arrows
                            }

                            "set" -> {
                                this.arrowsInBody = arrows
                            }

                            "dec" -> {
                                if (this.arrowsInBody >= 1) this.arrowsInBody -= arrows
                            }
                        }
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

}