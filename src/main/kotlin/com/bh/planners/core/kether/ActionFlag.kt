package com.bh.planners.core.kether

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.getFlag
import com.bh.planners.api.setFlag
import com.bh.planners.core.pojo.data.Data
import com.bh.planners.util.StringNumber
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.actions.LiteralAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionFlag {

    class DataGet(val action: ParsedAction<*>) : ScriptAction<Any>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Any> {
            return frame.newFrame(action).run<String>().thenApply {
                frame.asPlayer().plannersProfile.getFlag(it)!!.data
            }
        }

    }

    class DataSet(val action: ParsedAction<*>, val value: ParsedAction<*>, val time: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<String>().thenAccept { key ->
                frame.newFrame(value).run<Any>().thenAccept { value ->
                    val profile = frame.asPlayer().plannersProfile
                    frame.newFrame(time).run<Long>().thenAccept { time ->
                        profile.setFlag(key,Data(value, survivalStamp = time))
                    }
                }
            }
        }

    }

    class DataAdd(val action: ParsedAction<*>, val value: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<String>().thenAccept { key ->
                frame.newFrame(value).run<Any>().thenAccept { value ->
                    val dataContainer = frame.asPlayer().plannersProfile.flags
                    if (dataContainer.containsKey(key)) {
                        dataContainer.update(key, dataContainer[key]!!.increaseAny(value.toString()))
                    }
                }
            }
        }
    }

    companion object {

        /**
         * 取数据
         * flag *key
         * flag attackStamp
         *
         * 设置数据
         * flag *key to *value
         * flag attackStamp to time
         *
         * 设置数据 并附带存活时间
         * flag *key to *value timeout e *time
         * flag attack to time timeout 1000
         */
        @KetherParser(["flag"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            val keyAction = it.next(ArgTypes.ACTION)
            it.switch {
                case("to", "set") {
                    val valueAction = it.next(ArgTypes.ACTION)
                    val timeAction = try {
                        it.mark()
                        it.expects("timeout")
                        it.next(ArgTypes.ACTION)
                    } catch (_: Exception) {
                        it.reset()
                        ParsedAction(LiteralAction<Long>(-1L))
                    }
                    DataSet(keyAction, valueAction, timeAction)
                }
                case("add") {
                    DataAdd(keyAction, it.next(ArgTypes.ACTION))
                }
                other {
                    DataGet(keyAction)
                }
            }

        }

    }


}
