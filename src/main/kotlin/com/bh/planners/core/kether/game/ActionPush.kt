package com.bh.planners.core.kether.game

import com.bh.planners.api.entity.ProxyEntity
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.nextSelector
import com.bh.planners.core.kether.origin
import org.bukkit.Location
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionPush(
    val step: ParsedAction<*>,
    val selector: ParsedAction<*>,
    val pos: ParsedAction<*>?,
) : ScriptAction<Void>() {
    private fun execute(entity: ProxyEntity, locA: Location, locB: Location, step: Double) {
        val b = locB.clone()
        val vector1 = locA.direction.setY(0).setZ(0).normalize()
        vector1.multiply(b.subtract(locA).x)
        vector1.y = 0.0
        entity.velocity = vector1.multiply(step)
    }

    companion object {

        /**
         * push step selector1 selector2(1)
         */
        @KetherParser(["push"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionDrag(it.nextParsedAction(), it.nextParsedAction(), it.nextSelector())
        }

    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.run(step).double { step ->
            frame.createContainer(selector).thenAccept { container ->
                if (pos != null) {
                    frame.createContainer(pos).thenAccept {
                        val pos = it.firstLocation() ?: error("ActionDrag 'pos' empty")
                        container.forEachProxyEntity {
                            execute(this, this.location, pos, step)
                        }
                    }
                } else {
                    container.forEachProxyEntity {
                        execute(this, this.location, frame.origin().value, step)
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }
}

