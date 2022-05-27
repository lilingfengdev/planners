package com.bh.planners.core.kether

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import org.bukkit.entity.Player
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionSkill {

    class ActionSkillCast(val action: ParsedAction<*>, val markAction: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(action).run<String>().thenAccept { skill ->
                frame.newFrame(markAction).run<Boolean>().thenAccept { mark ->
                    val player = frame.script().sender!!.castSafely<Player>()!!
                    PlannersAPI.cast(player, skill, mark)
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class ActionSkillSwitch(val action: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(action).run<String>().thenAccept { skill ->
                val player = frame.script().sender!!.castSafely<Player>()!!
                frame.rootVariables()["@Skill"] = player.plannersProfile.getSkill(skill) ?: return@thenAccept
            }
            return CompletableFuture.completedFuture(null)
        }
    }


    companion object {

        @KetherParser(["skill"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            it.switch {
                case("name") {
                    actionNow {
                        getSkill().instance.option.name
                    }
                }
                case("level") {
                    actionNow {
                        mark()
                        try {
                            expect("cap")
                            getSkill().instance.option.levelCap
                        } catch (_: Exception) {
                            reset()
                            getSkill().level
                        }
                    }
                }
                case("key") {
                    actionNow {
                        // TODO mark wait
                        "Ctrl + sb"
                    }
                }
                case("name") {
                    actionNow {
                        getSkill().instance.option.name
                    }
                }
                case("switch") {
                    ActionSkillSwitch(it.next(ArgTypes.ACTION))
                }
                case("cast") {
                    actionNow {
                        ActionSkillCast(it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION))
                    }
                }


            }

        }

    }
}
