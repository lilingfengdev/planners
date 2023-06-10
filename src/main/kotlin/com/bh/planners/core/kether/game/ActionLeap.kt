package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.nextSelector
import com.bh.planners.core.kether.origin
import org.bukkit.Location
import org.bukkit.util.Vector
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionLeap(val step: ParsedAction<*>, val selector: ParsedAction<*>, val pos: ParsedAction<*>?) :
    ScriptAction<Void>() {
    private fun next(locA: Location, locB: Location, step: Double): Vector {
        val a = locA.clone()
        val b = locB.clone()
        a.y = 0.0
        b.y = 0.0
        val vectorAB = b.subtract(a).toVector()
        vectorAB.normalize()
        vectorAB.multiply(step)
        println("${vectorAB.x}-${vectorAB.y}-${vectorAB.z}")
        return vectorAB
    }

    companion object {

        /**
         * leap step selector1 selector2(1)
         */
        @KetherParser(["leap"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionDrag(it.nextParsedAction(), it.nextParsedAction(), it.nextSelector())
        }

    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.newFrame(step).run<Any>().thenAccept {
            val step = Coerce.toDouble(it)
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
    }
}

