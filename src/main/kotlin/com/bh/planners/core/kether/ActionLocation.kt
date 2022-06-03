package com.bh.planners.core.kether

import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionLocation {


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
         * loc [location] distance [location]
         */
        @KetherParser(["loc"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            val locAction = it.next(ArgTypes.ACTION)
            it.switch {
                case("distance") {
                    val target = it.next(ArgTypes.ACTION)
                    Distance(locAction, target)
                }
            }
        }


    }


}