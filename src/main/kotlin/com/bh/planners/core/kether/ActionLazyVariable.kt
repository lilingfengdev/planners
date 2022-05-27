package com.bh.planners.core.kether

import taboolib.common.platform.function.info
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionLazyVariable {

    class VariableGet(val action: ParsedAction<*>) : ScriptAction<Any>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Any> {
            return frame.newFrame(action).run<String>().thenApply {
                frame.rootVariables().get<LazyGetter<*>>(it).get().get()
            }
        }
    }

    class VariableReload(val action: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(action).run<String>().thenAccept {
                frame.rootVariables().get<LazyGetter<*>>(it).get().reload()
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    companion object {

        @KetherParser(["lazy"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            it.switch {
                case("get") {
                    VariableGet(it.next(ArgTypes.ACTION))
                }

                case("reload") {
                    VariableReload(it.next(ArgTypes.ACTION))
                }

                other {
                    VariableGet(it.next(ArgTypes.ACTION))
                }
            }
        }

    }

}
