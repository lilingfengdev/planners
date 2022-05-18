package com.bh.planners.core.kether

import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionVariable {

    class VariableGet(val action: ParsedAction<*>) : ScriptAction<Any>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Any> {
            return frame.newFrame(action).run<String>().thenApply {
                val session = frame.getSession()
                session.getLazyVariable(it)
            }
        }


    }

    companion object {

        @KetherParser(["var"], namespace = "Planners")
        fun parser() = scriptParser {
            it.switch {
                case("var") {
                    VariableGet(it.next(ArgTypes.ACTION))
                }
            }
        }

    }

}
