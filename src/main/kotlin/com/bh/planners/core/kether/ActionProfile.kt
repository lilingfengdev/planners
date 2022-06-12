package com.bh.planners.core.kether

import com.bh.planners.api.ManaCounter.addMana
import com.bh.planners.api.ManaCounter.setMana
import com.bh.planners.api.ManaCounter.toCurrentMana
import com.bh.planners.api.ManaCounter.toMaxMana
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.addPoint
import com.bh.planners.api.combat.Combat.isCombat
import com.bh.planners.api.combat.Combat.isCombatLocal
import com.bh.planners.api.setPoint
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionProfile {

    class PointOperation(val action: ParsedAction<*>, val operator: Operator) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<Any>().thenAccept {
                val profile = frame.asPlayer().plannersProfile
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
                val profile = frame.asPlayer().plannersProfile
                when (operator) {
                    Operator.ADD -> profile.addMana(Coerce.toDouble(it))
                    Operator.TAKE -> profile.addMana(-Coerce.toDouble(it))
                    Operator.SET -> profile.setMana(Coerce.toDouble(it))
                }
            }
        }
    }


    companion object {

        @KetherParser(["profile"], namespace = NAMESPACE)
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
                        script().sender!!.cast<Player>().plannersProfile.job?.name
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
                case("combat") {
                    actionNow {
                        script().sender!!.cast<Player>().isCombat
                    }
                }
                case("combat-local") {
                    actionNow {
                        script().sender!!.cast<Player>().isCombatLocal
                    }
                }

            }
        }

    }

    enum class Operator {
        ADD, TAKE, SET
    }

}