package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.nextSelector
import com.bh.planners.core.kether.origin
import org.bukkit.Location
import org.bukkit.util.Vector
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionDrag(
    val step: ParsedAction<*>,
    val selector: ParsedAction<*>,
    val pos: ParsedAction<*>?,
) : ScriptAction<Void>() {


    private fun next(locA: Location, locB: Location, step: Double): Vector {
        val vectorAB = locB.clone().subtract(locA).toVector()
        vectorAB.normalize()
        vectorAB.multiply(step)
        return vectorAB
    }

    companion object {

        /**
         * drag step selector1 selector2(1)
         */
        @KetherParser(["drag"], namespace = NAMESPACE, shared = true)
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
                            this.velocity = next(this.location, pos, step)
                        }
                    }
                } else {
                    container.forEachProxyEntity {
                        this.velocity = next(this.location, frame.origin().value, step)
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }


}