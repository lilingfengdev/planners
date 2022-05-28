package com.bh.planners.core.kether

import taboolib.common.platform.function.submit
import taboolib.library.kether.*
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionTimer {

    class TimerAsync(val action: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            submit(true) {
                frame.newFrame(action).run<Any>()
            }

            return CompletableFuture.completedFuture(null)
        }


    }

    class TimerAwait<T>(val action: ParsedAction<*>) : ScriptAction<T>() {
        override fun run(frame: ScriptFrame): CompletableFuture<T> {
            val future = CompletableFuture<T>()
            frame.newFrame(action).run<T>().thenAccept(QuestFuture.complete(future))
            return future
        }
    }

    companion object {

        @KetherParser(["timer"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            it.switch {
                case("async") {
                    TimerAsync(it.next(ArgTypes.ACTION))
                }
                case("await") {
                    TimerAwait<Any>(it.next(ArgTypes.ACTION))
                }
                other {
                    TimerAsync(it.next(ArgTypes.ACTION))
                }
            }
        }


    }

}