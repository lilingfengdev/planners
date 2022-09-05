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
            return frame.newFrame(action).run<Any>().thenApply {
                val optional = frame.rootVariables().get<LazyGetter<*>>(it.toString())
                if (optional.isPresent) {
                    optional.get().get()
                } else "empty $it"
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

        /**
         * 在技能释放环境食用
         *
         * 取技能的variable
         * lazy get [var]
         * lazy get abc
         *
         * 重载技能的variable
         * lazy reload [var]
         * lazy reload abc
         */
        @KetherParser(["lazy"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("get") {
                    VariableGet(it.nextParsedAction())
                }

                case("reload") {
                    VariableReload(it.nextParsedAction())

                }

                other {
                    VariableGet(it.nextParsedAction())
                }
            }
        }

    }

}
