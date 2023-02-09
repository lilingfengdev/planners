package com.bh.planners.core.kether

import com.bh.planners.api.ManaCounter.addMana
import com.bh.planners.api.ManaCounter.setMana
import com.bh.planners.api.ManaCounter.toCurrentMana
import com.bh.planners.api.ManaCounter.toMaxMana
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.api.addPoint
import com.bh.planners.api.common.Operator
import com.bh.planners.api.getFlag
import com.bh.planners.api.setFlag
import com.bh.planners.api.setPoint
import com.bh.planners.core.pojo.data.Data
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionProfile {

    class PointOperation(val action: ParsedAction<*>, val operator: Operator) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<Any>().thenAccept {
                val profile = frame.senderPlannerProfile() ?: return@thenAccept
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
                val profile = frame.senderPlannerProfile() ?: return@thenAccept
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

                frame.senderPlannerProfile()?.getFlag(it.toString())?.data
            }
        }

    }

    class DataSet(val action: ParsedAction<*>, val value: ParsedAction<*>, val time: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<Any>().thenAccept { key ->
                frame.newFrame(value).run<Any>().thenAccept { value ->
                    val profile = frame.senderPlannerProfile() ?: return@thenAccept
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
                    val dataContainer = frame.senderPlannerProfile()?.flags ?: return@thenAccept
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
                val dataContainer = frame.senderPlannerProfile()?.flags ?: return@thenApply false
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
                            "take", "-=" -> ManaOperation(it.nextParsedAction(), Operator.TAKE)
                            "add", "+=" -> ManaOperation(it.nextParsedAction(), Operator.ADD)
                            "set", "=" -> PointOperation(it.nextParsedAction(), Operator.SET)
                            else -> error("out of case")
                        }
                    } catch (e: Throwable) {
                        reset()
                        actionNow {
                            script().sender!!.cast<Player>().toCurrentMana()
                        }
                    }

                }
                case("health-percent") {
                    actionNow {
                        val player = script().sender!!.cast<Player>()
                        try {
                            player.health / player.maxHealth
                        }catch (e: Exception) {
                            0.0
                        }
                    }
                }
                case("mana-percent") {
                    actionNow {
                        val player = script().sender!!.cast<Player>()
                        if (!player.plannersProfileIsLoaded) return@actionNow 0.0
                        val maxMana = player.toMaxMana()
                        if (maxMana == 0.0) {
                            return@actionNow 0.0
                        }
                        try {
                            player.toCurrentMana() / maxMana
                        }catch (e: Exception) {
                            0.0
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
                            "take", "-=" -> PointOperation(it.nextParsedAction(), Operator.TAKE)
                            "set", "=" -> PointOperation(it.nextParsedAction(), Operator.SET)
                            "add", "+=" -> PointOperation(it.nextParsedAction(), Operator.ADD)
                            else -> error("out of case")
                        }
                    } catch (e: Throwable) {
                        reset()
                        actionNow { senderPlannerProfile()?.point }
                    }

                }
                case("job") {
                    actionNow { senderPlannerProfile()?.job?.name ?: "暂无" }
                }
                case("level") {
                    actionNow { senderPlannerProfile()?.level }
                }
                case("level-length") {
                    actionNow { senderPlannerProfile()?.level?.toString()?.length }
                }
                case("exp", "experience") {
                    actionNow { senderPlannerProfile()?.experience }
                }

                case("exp-percent") {
                    actionNow {
                        val profile = senderPlannerProfile() ?: return@actionNow 0
                        try {
                            profile.experience / profile.maxExperience
                        }catch (e: Exception) {
                            0.0
                        }
                    }
                }

                case("max-exp", "max-experience") {
                    actionNow { senderPlannerProfile()?.maxExperience }
                }

                case("flag", "data") {
                    val key = it.nextParsedAction()
                    try {
                        mark()
                        when (expects("add", "set", "get", "to", "has")) {
                            "set", "to" -> {
                                DataSet(key, it.nextParsedAction(), it.tryGet(arrayOf("timeout"), -1L)!!)
                            }

                            "get" -> DataGet(key)
                            "add" -> DataAdd(key, it.nextParsedAction())
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