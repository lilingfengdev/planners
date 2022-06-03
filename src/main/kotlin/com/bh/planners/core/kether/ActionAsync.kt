package com.bh.planners.core.kether

import taboolib.common.platform.function.submit
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionAsync {


    class TimerAsync(val action: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            submit(async = true) {
                frame.newFrame(action).run<Any>().get()
            }

            return CompletableFuture.completedFuture(null)
        }


    }


//    companion object {
//
//        @KetherParser(["async"], namespace = NAMESPACE)
//        fun parser() = scriptParser {
//            TimerAsync(it.next(ArgTypes.ACTION))
//        }
//
//
//    }


}