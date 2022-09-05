package com.bh.planners.core.kether.util

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.toLocation
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class Locations {


    class Distance(val loc: ParsedAction<*>, val target: ParsedAction<*>) : ScriptAction<Double>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Double> {

            val future = CompletableFuture<Double>()

            frame.newFrame(loc).run<Any>().thenAccept {
                val location = it.toLocation()
                frame.newFrame(target).run<Any>().thenAccept { target ->
                    try {
                        val targetLocation = target.toLocation()
                        val distance = location.distance(targetLocation)
                        future.complete(distance)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            return future
        }
    }

    companion object {

        /**
         * distance [location] and [location]
         */
        @KetherParser(["distance"], namespace = NAMESPACE, shared = true)
        fun parser1() = scriptParser {
            val pos1 = it.nextParsedAction()
            it.expect("and")
            val pos2 = it.nextParsedAction()
            Distance(pos1, pos2)
        }

    }


}