package com.bh.planners.core.kether

import com.bh.planners.api.PlannersAPI.profile
import com.bh.planners.core.pojo.data.Data
import com.bh.planners.util.StringNumber
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.actions.LiteralAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionData {

    class DataGet(val action: ParsedAction<*>) : ScriptAction<Any>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Any> {
            return frame.newFrame(action).run<String>().thenApply {
                frame.script().sender!!.cast<Player>().profile().dataContainer[it]!!.data
            }
        }

    }

    class DataSet(val action: ParsedAction<*>, val value: ParsedAction<*>, val time: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<String>().thenAccept { key ->
                frame.newFrame(value).run<Any>().thenAccept { value ->
                    val profile = frame.script().sender!!.cast<Player>().profile()
                    frame.newFrame(time).run<Long>().thenAccept { time ->
                        profile.dataContainer[key] = Data(value, survivalStamp = time)
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
                    val dataContainer = frame.script().sender!!.cast<Player>().profile().dataContainer
                    if (dataContainer.containsKey(key)) {
                        dataContainer.update(key, dataContainer[key]!!.increaseAny(value.toString()))
                    }
                }
            }
        }
    }

    companion object {

        /**
         * data *key
         * data *key to *value
         * data *key to *value time e *time
         */
        @KetherParser(["data"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            val keyAction = it.next(ArgTypes.ACTION)
            it.switch {
                case("to", "set") {
                    val valueAction = it.next(ArgTypes.ACTION)
                    val timeAction = try {
                        it.mark()
                        it.expects("time")
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
