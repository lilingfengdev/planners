package com.bh.planners.core.kether

import taboolib.common.platform.function.submit
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestFuture
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionAwait {



    class TimerAwait<T>(val action: ParsedAction<*>) : ScriptAction<T>() {
        override fun run(frame: ScriptFrame): CompletableFuture<T> {
            val future = CompletableFuture<T>()
            frame.newFrame(action).run<T>().thenAccept(QuestFuture.complete(future))
            return future
        }
    }



//    companion object {
//
//        @KetherParser(["await"], namespace = NAMESPACE)
//        fun parser() = scriptParser {
//            TimerAwait<Any>(it.next(ArgTypes.ACTION))
//        }
//
//
//    }




}