package com.bh.planners.core.kether

import com.bh.planners.api.EntityAPI.getFlag
import com.bh.planners.api.addPoint
import com.bh.planners.api.setFlag
import com.bh.planners.api.setPoint
import com.bh.planners.core.module.mana.ManaManager
import com.bh.planners.core.pojo.data.Data
import taboolib.common5.Coerce
import taboolib.common5.cdouble
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionProfile {


    class DataGet(val action: ParsedAction<*>, val default: ParsedAction<*>) : ScriptAction<Any>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Any> {

            val future = CompletableFuture<Any>()
            frame.run(action).str { id ->
                frame.run(default).thenAccept { def ->
                    future.complete(frame.bukkitPlayer()?.getFlag(id)?.data ?: def)
                }
            }
            return future
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

    class DataAdd(val action: ParsedAction<*>, val value: ParsedAction<*>) : ScriptAction<Void>() {
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
                            "take", "-=" -> actionProfileTake(it.nextParsedAction()) { value, profile ->
                                ManaManager.INSTANCE.takeMana(profile,value.cdouble)
                            }

                            "add", "+=" -> actionProfileTake(it.nextParsedAction()) { value, profile ->
                                ManaManager.INSTANCE.addMana(profile,value.cdouble)
                            }

                            "set", "=" -> actionProfileTake(it.nextParsedAction()) { value, profile ->
                                ManaManager.INSTANCE.setMana(profile,value.cdouble)
                            }

                            else -> error("out of case")
                        }
                    } catch (e: Throwable) {
                        reset()
                        actionProfileNow { ManaManager.INSTANCE.getMana(it) }
                    }

                }
                case("health-percent") {
                    actionProfileNow { it.player.health / it.player.maxHealth }
                }
                case("mana-percent") {
                    actionProfileNow {
                        try {
                            ManaManager.INSTANCE.getMana(it) / ManaManager.INSTANCE.getMaxMana(it)
                        } catch (_: Exception) {
                            0.0
                        }
                    }
                }
                case("max-mana") {
                    actionProfileNow {
                        try {
                            ManaManager.INSTANCE.getMaxMana(it)
                        } catch (_: Exception) {
                            0.0
                        }
                    }
                }
                case("point") {
                    try {
                        mark()
                        when (expects("take", "-=", "set", "=", "add", "+=")) {
                            "take", "-=" -> actionProfileTake(it.nextParsedAction()) { value, profile ->
                                profile.addPoint(-Coerce.toInteger(value))
                            }

                            "set", "=" -> actionProfileTake(it.nextParsedAction()) { value, profile ->
                                profile.setPoint(Coerce.toInteger(value))
                            }

                            "add", "+=" -> actionProfileTake(it.nextParsedAction()) { value, profile ->
                                profile.addPoint(Coerce.toInteger(value))
                            }

                            else -> error("out of case")
                        }
                    } catch (e: Throwable) {
                        reset()
                        actionProfileNow { it.point }
                    }

                }
                case("job") {
                    actionProfileNow { it.job?.name ?: "暂无" }
                }
                case("level") {
                    actionProfileNow { it.job?.level ?: 0 }
                }
                case("level-length") {
                    actionProfileNow { it.job?.level?.toString()?.length ?: 0 }
                }
                case("exp", "experience") {
                    actionProfileNow { it.experience }
                }

                case("exp-percent") {
                    actionProfileNow {
                        try {
                            (it.experience.toDouble() / it.maxExperience.toDouble())
                        } catch (_: Throwable) {
                            0.0
                        }
                    }
                }

                case("max-exp", "max-experience") {
                    actionProfileNow { it.maxExperience }
                }

                case("flag", "data") {
                    val key = it.nextParsedAction()
                    try {
                        mark()
                        when (expects("add", "set", "get", "to", "has")) {
                            "set", "to" -> {
                                DataSet(key, it.nextParsedAction(), it.nextArgumentAction(arrayOf("timeout"), -1L)!!)
                            }

                            "get" -> DataGet(key, it.nextArgumentAction(arrayOf("def", "--def"), "null")!!)
                            "add" -> DataAdd(key, it.nextParsedAction())
                            "has" -> DataHas(key)
                            else -> error("error of case!")
                        }
                    } catch (_: Throwable) {
                        reset()
                        DataGet(key, it.nextArgumentAction(arrayOf("def", "--def"), "null")!!)
                    }
                }

            }
        }

    }

}