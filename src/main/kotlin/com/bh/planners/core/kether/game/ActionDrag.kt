package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.selector
import com.bh.planners.core.kether.origin
import org.bukkit.Location
import org.bukkit.util.Vector
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionDrag(val step: ParsedAction<*>, val selector: ParsedAction<*>, val pos: ParsedAction<*>?) :
    ScriptAction<Void>() {


    fun next(locA: Location, locB: Location, step: Double): Vector {
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
            ActionDrag(it.nextParsedAction(), it.nextParsedAction(), it.selector())
        }

    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.newFrame(step).run<Any>().thenAccept {
            val step = Coerce.toDouble(it)
            frame.createContainer(selector).thenAccept { container ->
                if (pos != null) {
                    frame.createContainer(pos).thenAccept {
                        val pos = it.firstLocationTarget() ?: error("ActionDrag 'pos' empty")
                        container.forEachEntity {
                            this.velocity = next(this.location, pos, step)
                        }
                    }
                } else {
                    container.forEachEntity {
                        this.velocity = next(this.location, frame.origin()!!.value, step)
                    }
                }

            }
        }
    }


}