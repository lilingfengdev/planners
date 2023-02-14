package com.bh.planners.core.kether

import com.bh.planners.api.ManaCounter.addMana
import com.bh.planners.api.ManaCounter.setMana
import com.bh.planners.api.ManaCounter.takeMana
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
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestContext
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionProfile {

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
                            "take", "-=" -> actionProfileTake(it.nextParsedAction()) { value, profile ->
                                profile.takeMana(Coerce.toDouble(value))
                            }

                            "add", "+=" -> actionProfileTake(it.nextParsedAction()) { value, profile ->
                                profile.addMana(Coerce.toDouble(value))
                            }

                            "set", "=" -> actionProfileTake(it.nextParsedAction()) { value, profile ->
                                profile.setMana(Coerce.toDouble(value))
                            }

                            else -> error("out of case")
                        }
                    } catch (e: Throwable) {
                        reset()
                        actionProfileNow { it.toCurrentMana() }
                    }

                }
                case("health-percent") {
                    actionProfileNow { it.player.health / it.player.maxHealth }
                }
                case("mana-percent") {
                    actionProfileNow {
                        it.toCurrentMana() / it.toMaxMana()
                    }
                }
                case("max-mana") {
                    actionProfileNow { it.toMaxMana() }
                }
                case("point") {
                    try {
                        mark()
                        when (expects("take", "-=", "set", "=", "add", "+=")) {
                            "take", "-=" -> actionProfileTake(it.nextParsedAction()) { value,profile ->
                                profile.addPoint(-Coerce.toInteger(value))
                            }
                            "set", "=" -> actionProfileTake(it.nextParsedAction()) { value,profile ->
                                profile.setPoint(Coerce.toInteger(value))
                            }
                            "add", "+=" -> actionProfileTake(it.nextParsedAction()) { value,profile ->
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
                    actionProfileNow { it.job?.name }
                }
                case("level") {
                    actionProfileNow { it.job?.level }
                }
                case("level-length") {
                    actionProfileNow { it.job?.level?.toString()?.length ?: 0 }
                }
                case("exp", "experience") {
                    actionProfileNow { it.experience }
                }

                case("exp-percent") {
                    actionProfileNow { it.experience / it.maxExperience }
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
                                DataSet(key, it.nextParsedAction(), it.tryGet(arrayOf("timeout"), -1L)!!)
                            }

                            "get" -> actionProfileTake(key) { value,profile ->
                                profile.getFlag(value.toString())
                            }
                            "add" -> DataAdd(key, it.nextParsedAction())
                            "has" -> DataHas(key)
                            else -> error("error of case!")
                        }
                    } catch (_: Throwable) {
                        reset()
                        actionProfileTake(key) { value,profile ->
                            profile.getFlag(value.toString())
                        }
                    }
                }

            }
        }

        fun actionProfileNow(func: QuestContext.Frame.(PlayerProfile) -> Any?) = actionFuture { future ->
            val player = this.bukkitPlayer() ?: error("No player selected.")
            if (!player.plannersProfileIsLoaded) {
                future.complete("__LOADED__")
                return@actionFuture future
            }
            future.complete(func(this, player.plannersProfile))
        }

        fun actionProfileTake(
            action: ParsedAction<*>,
            func: QuestContext.Frame.(value: Any, profile: PlayerProfile) -> Unit
        ) = actionTake {
            val player = this.bukkitPlayer() ?: error("No player selected.")
            if (!player.plannersProfileIsLoaded) {
                return@actionTake CompletableFuture.completedFuture(null)
            }

            this.run(action).thenAccept {
                func(this, it!!, player.plannersProfile)
            }

            return@actionTake CompletableFuture.completedFuture(null)
        }

    }


}