package com.bh.planners.core.kether

import com.bh.planners.core.pojo.data.Data
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionLazyVariable {

    class VariableGet(val action: ParsedAction<*>) : ScriptAction<Any>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Any> {
            return frame.newFrame(action).run<Any>().thenApply {
                frame.runVariable(it.toString())
            }
        }
    }

    class VariableReload(val action: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(action).run<String>().thenAccept {
                frame.rootVariables().get<Data>(it).ifPresent { it.toLazyGetter().reload() }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    companion object {

        fun ScriptFrame.runVariable(id: String): Any? {
            return if (rootVariables().keys().contains("__${id}_VARIABLE")) {
                rootVariables().get<Any>("__${id}_VARIABLE").get()
            } else {
                val optional = rootVariables().get<Data>(id)
                if (optional.isPresent) {
                    optional.get().toLazyGetter().get().also {
                        this.rootVariables()["__${id}_VARIABLE"] = it
                    }
                } else error("Not found variable $id")
            }


        }

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
