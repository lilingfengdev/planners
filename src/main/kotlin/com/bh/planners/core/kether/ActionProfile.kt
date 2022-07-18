package com.bh.planners.core.kether

import com.bh.planners.api.ManaCounter.addMana
import com.bh.planners.api.ManaCounter.setMana
import com.bh.planners.api.ManaCounter.toCurrentMana
import com.bh.planners.api.ManaCounter.toMaxMana
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.addPoint
import com.bh.planners.api.common.Operator
import com.bh.planners.api.getFlag
import com.bh.planners.api.setFlag
import com.bh.planners.api.setPoint
import com.bh.planners.core.pojo.data.Data
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.actions.LiteralAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionProfile {

    class PointOperation(val action: ParsedAction<*>, val operator: Operator) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<Any>().thenAccept {
                val profile = frame.asPlayer()!!.plannersProfile
                when (operator) {
                    Operator.ADD -> profile.addPoint(Coerce.toInteger(it))
                    Operator.TAKE -> profile.addPoint(-Coerce.toInteger(it))
                    Operator.SET -> profile.setPoint(Coerce.toInteger(it))
                }
            }
        }
    }

    class ManaOperation(val action: ParsedAction<*>, val operator: Operator) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<Any>().thenAccept {
                val profile = frame.asPlayer()!!.plannersProfile
                when (operator) {
                    Operator.ADD -> profile.addMana(Coerce.toDouble(it))
                    Operator.TAKE -> profile.addMana(-Coerce.toDouble(it))
                    Operator.SET -> profile.setMana(Coerce.toDouble(it))
                }
            }
        }
    }


    class DataGet(val action: ParsedAction<*>) : ScriptAction<Any?>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Any?> {
            return frame.newFrame(action).run<Any>().thenApply {
                frame.asPlayer()!!.plannersProfile.getFlag(it.toString())?.data
            }
        }

    }

    class DataSet(val action: ParsedAction<*>, val value: ParsedAction<*>, val time: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<Any>().thenAccept { key ->
                frame.newFrame(value).run<Any>().thenAccept { value ->
                    val profile = frame.asPlayer()!!.plannersProfile
                    frame.newFrame(time).run<Any>().thenAccept { time ->
                        profile.setFlag(key.toString(), Data(value, survivalStamp = Coerce.toLong(time) * 50))
                    }
                }
            }
        }

    }

    class DataAdd(val action: ParsedAction<*>, val value: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<Any>().thenAccept {
                val key = it.toString()
                frame.newFrame(value).run<Any>().thenAccept { value ->
                    val dataContainer = frame.asPlayer()!!.plannersProfile.flags
                    if (dataContainer.containsKey(key)) {
                        dataContainer.update(key, dataContainer[key]!!.increaseAny(value.toString()))
                    }
                }
            }
        }
    }

    class DataHas(val action: ParsedAction<*>) : ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(action).run<Any>().thenApply {
                val key = it.toString()
                val dataContainer = frame.asPlayer()!!.plannersProfile.flags
                dataContainer.containsKey(key)
            }
        }
    }


    companion object {


        /**
         * 取数据
         * profile flag [key : action]
         *
         * 设置数据
         * profile flag [key: action] to [value: action]
         *
         * 设置数据 并附带存活时间
         * profile flag [key: action] to [value: action] <timeout [time: action]>
         *
         * 是否存在数据
         * profile flag [key : action]
         *
         */
        @KetherParser(["profile"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("mana") {
                    try {
                        mark()
                        when (expects("take", "-=", "add", "+=", "set", "=")) {
                            "take", "-=" -> ManaOperation(next(ArgTypes.ACTION), Operator.TAKE)
                            "add", "+=" -> ManaOperation(next(ArgTypes.ACTION), Operator.ADD)
                            "set", "=" -> PointOperation(next(ArgTypes.ACTION), Operator.SET)
                            else -> error("out of case")
                        }
                    } catch (e: Throwable) {
                        reset()
                        actionNow {
                            script().sender!!.cast<Player>().toCurrentMana()
                        }
                    }

                }
                case("max-mana") {
                    actionNow {
                        script().sender!!.cast<Player>().toMaxMana()
                    }
                }
                case("point") {
                    try {
                        mark()
                        when (expects("take", "-=", "set", "=", "add", "+=")) {
                            "take", "-=" -> PointOperation(next(ArgTypes.ACTION), Operator.TAKE)
                            "set", "=" -> PointOperation(next(ArgTypes.ACTION), Operator.SET)
                            "add", "+=" -> PointOperation(next(ArgTypes.ACTION), Operator.ADD)
                            else -> error("out of case")
                        }
                    } catch (e: Throwable) {
                        reset()
                        actionNow {
                            script().sender!!.cast<Player>().plannersProfile.point
                        }
                    }

                }
                case("job") {
                    actionNow {
                        script().sender!!.cast<Player>().plannersProfile.job?.name ?: "暂无"
                    }
                }
                case("level") {
                    actionNow {
                        script().sender!!.cast<Player>().plannersProfile.level
                    }
                }
                case("exp", "experience") {
                    actionNow {
                        script().sender!!.cast<Player>().plannersProfile.experience
                    }
                }

                case("max-exp", "max-experience") {
                    actionNow {
                        script().sender!!.cast<Player>().plannersProfile.maxExperience
                    }
                }

                case("flag", "data") {
                    val key = it.next(ArgTypes.ACTION)
                    try {
                        mark()
                        when (expects("add", "set", "get", "to", "has")) {
                            "set", "to" -> {
                                DataSet(key, it.next(ArgTypes.ACTION), it.tryGet(arrayOf("timeout"), -1L)!!)
                            }

                            "get" -> DataGet(key)
                            "add" -> DataAdd(key, it.next(ArgTypes.ACTION))
                            "has" -> DataHas(key)
                            else -> error("error of case!")
                        }
                    } catch (_: Throwable) {
                        reset()
                        DataGet(key)
                    }
                }

            }
        }

    }


}