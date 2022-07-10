package com.bh.planners.core.kether

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.getFlag
import com.bh.planners.api.setFlag
import com.bh.planners.core.pojo.data.Data
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.actions.LiteralAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionFlag {

    class DataSet(
        val action: ParsedAction<*>,
        val value: ParsedAction<*>,
        val time: ParsedAction<*>,
        val selector: ParsedAction<*>?
    ) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<Any>().thenAccept { key ->
                frame.newFrame(value).run<Any>().thenAccept { value ->

                    frame.newFrame(time).run<Any>().thenAccept { time ->
                        if (selector != null) {
                            frame.execPlayer(selector) {
                                plannersProfile.setFlag(
                                    key.toString(), Data(value, survivalStamp = Coerce.toLong(time))
                                )
                            }
                        } else {
                            val profile = frame.asPlayer()!!.plannersProfile
                            profile.setFlag(key.toString(), Data(value, survivalStamp = Coerce.toLong(time)))
                        }

                    }
                }
            }
        }

    }

    class DataAdd(val action: ParsedAction<*>, val value: ParsedAction<*>, val selector: ParsedAction<*>?) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<Any>().thenAccept {
                val key = it.toString()
                frame.newFrame(value).run<Any>().thenAccept { value ->
                    if (selector != null) {
                        frame.execPlayer(selector) {
                            val dataContainer = plannersProfile.flags
                            if (dataContainer.containsKey(key)) {
                                dataContainer.update(key, dataContainer[key]!!.increaseAny(value.toString()))
                            }
                        }
                    } else {
                        val dataContainer = frame.asPlayer()!!.plannersProfile.flags
                        if (dataContainer.containsKey(key)) {
                            dataContainer.update(key, dataContainer[key]!!.increaseAny(value.toString()))
                        }
                    }

                }
            }
        }
    }

    companion object {


        /**
         * 设置数据
         * flag [key: action] to [value: action] <selector>
         *
         * 设置数据 并附带存活时间
         * flag [key: action] to [value: action] <timeout [time: action]>  <selector>
         *
         * 是否存在数据
         * flag [key: action] add [value: action]  <selector>
         *
         */
        @KetherParser(["flag", "data"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            val key = it.next(ArgTypes.ACTION)
            try {
                it.mark()
                when (it.expects("add", "set")) {
                    "set", "to" -> {
                        val value = it.next(ArgTypes.ACTION)
                        val timeout = try {
                            it.mark()
                            it.expects("timeout")
                            it.next(ArgTypes.ACTION)
                        } catch (_: Throwable) {
                            it.reset()
                            ParsedAction(LiteralAction<Long>(-1L))
                        }
                        DataSet(key, value, timeout, it.selectorAction())
                    }

                    "add" -> DataAdd(key, it.next(ArgTypes.ACTION), it.selectorAction())

                    else -> error("error of case!")
                }
            } catch (_: Throwable) {
                it.reset()
                error("error of case!")
            }
        }

    }

}