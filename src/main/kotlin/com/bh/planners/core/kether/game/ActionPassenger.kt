package com.bh.planners.core.kether.game

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.containerOrSender
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.nextSelectorOrNull
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture


class ActionPassenger {

    class ActionAddPassenger(
        val passenger: ParsedAction<*>,
        val selector: ParsedAction<*>?,
    ) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.createContainer(passenger).thenAccept { container ->
                frame.containerOrSender(selector).thenAccept { the ->
                    container.forEachEntity {
                        val rider = this
                        the.forEachEntity {
                            addPassenger(rider)
                        }
                    }
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class ActionRemovePassenger(
        val passenger: ParsedAction<*>,
        val selector: ParsedAction<*>?,
    ) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.createContainer(passenger).thenAccept { container ->
                frame.containerOrSender(selector).thenAccept { those ->
                    container.forEachEntity {
                        val rider = this
                        those.forEachEntity {
                            removePassenger(rider)
                        }
                    }
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }


    class ActionGetPassengers(
        val selector: ParsedAction<*>?,
    ) : ScriptAction<Target.Container>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Target.Container> {
            val future = CompletableFuture<Target.Container>()
            frame.containerOrSender(selector).thenAccept {
                val container = Target.Container()
                it.forEachEntity {
                    passengers.forEach {
                        container += it.toTarget()
                    }
                }
                future.complete(container)
            }
            return future
        }
    }


    companion object {

        /**
         * passengers add selector1 [selector]
         * passengers remove selector1 [selector]
         * passengers get [selector]
         */
        @KetherParser(["passengers"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("add") {
                    ActionAddPassenger(it.nextParsedAction(), it.nextSelectorOrNull())
                }
                case("remove") {
                    ActionRemovePassenger(it.nextParsedAction(), it.nextSelectorOrNull())
                }
                case("get") {
                    ActionGetPassengers(it.nextSelectorOrNull())
                }
            }
        }
    }

}
