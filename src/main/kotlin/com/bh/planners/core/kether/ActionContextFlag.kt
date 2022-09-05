package com.bh.planners.core.kether

import com.bh.planners.core.pojo.data.Data
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionContextFlag {

    class ContextDataGet(val action: ParsedAction<*>) : ScriptAction<Any?>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Any?> {
            return frame.newFrame(action).run<Any>().thenApply {
                frame.getContext().flags[it.toString()]?.data
            }
        }

    }

    class ContextDataSet(val action: ParsedAction<*>, val value: ParsedAction<*>, val time: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<Any>().thenAccept {
                val key = it.toString()
                frame.newFrame(value).run<Any>().thenAccept { value ->
                    frame.newFrame(time).run<Any>().thenAccept { time ->
                        frame.getContext().flags[key] = Data(value, survivalStamp = Coerce.toLong(time))
                    }
                }
            }
        }

    }

    class ContextDataAdd(val action: ParsedAction<*>, val value: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<Any>().thenAccept {
                val key = it.toString()
                frame.newFrame(value).run<Any>().thenAccept { value ->
                    val dataContainer = frame.getContext().flags
                    if (dataContainer.containsKey(key)) {
                        dataContainer.update(key, dataContainer[key]!!.increaseAny(value.toString()))
                    }
                }
            }
        }
    }

    class ContextDataHas(val action: ParsedAction<*>) : ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(action).run<Any>().thenApply { key ->
                frame.getContext().flags.containsKey(key.toString())
            }
        }
    }

    companion object {

        /**
         * 取数据
         * context flag [key: action]
         *
         * 设置数据
         * context flag [key: action] to [value: action]
         *
         * 设置数据 并附带存活时间
         * context flag [key: action] to [value: action] <timeout [time: action]>
         */
        @KetherParser(["context"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("flag") {
                    val key = it.nextParsedAction()
                    try {
                        mark()
                        when (expects("add", "set", "get", "to", "has")) {
                            "set", "to" -> {
                                val value = it.nextParsedAction()
                                ContextDataSet(key, value, tryGet(arrayOf("timeout"),-1)!!)
                            }

                            "get" -> ContextDataGet(key)
                            "add" -> ContextDataAdd(key, it.nextParsedAction())
                            "has" -> ContextDataHas(key)
                            else -> error("error of case!")
                        }
                    } catch (_: Throwable) {
                        reset()
                        ContextDataGet(key)
                    }
                }
            }

        }

    }


}
