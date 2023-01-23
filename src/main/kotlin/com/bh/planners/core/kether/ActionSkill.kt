package com.bh.planners.core.kether

import com.bh.planners.api.ContextAPI
import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.common.Operator
import com.bh.planners.api.Counting
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.maxLevel
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.player.PlayerJob
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestContext
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionSkill {

    class CooldownOperator(val operator: Operator, val amount: ParsedAction<*>, val target: ParsedAction<*>?) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.readAccept<Long>(amount) { amount ->
                if (target != null) {
                    frame.readAccept<String>(target) {
                        val skill = PlannersAPI.getSkill(it) ?: return@readAccept
                        execute(frame.bukkitPlayer() ?: return@readAccept, skill, operator, amount * 50)
                    }
                } else {
                    execute(frame.bukkitPlayer() ?: return@readAccept, frame.skill().instance, operator, amount * 50)
                }
            }

            return CompletableFuture.completedFuture(null)
        }
    }

    class LevelGet(val of: ParsedAction<*>?) : ScriptAction<Int>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Int> {
            val future = CompletableFuture<Int>()

            if (of != null) {
                frame.run(of).str { skill ->
                    future.complete(frame.bukkitPlayer()?.plannersProfile?.getSkill(skill)?.level ?: -1)
                }
            } else {
                future.complete(frame.skill().level)
            }

            return future
        }

    }

    companion object {

        /**
         *
         * 必须在技能冷却期间才有效
         *
         * 给予冷却时间/ms of可选 填写即指定技能 否则即当前环境应用技能
         * skill cooldown add 10 [of ""]
         * 扣除冷却时间/ms of可选 填写即指定技能 否则即当前环境应用技能
         * skill cooldown take 10 [of ""]
         * 设置冷却时间/ms (不管在任何情况下都设置成传入时间) of可选 填写即指定技能 否则即当前环境应用技能
         * skill cooldown set 10 [of ""]
         *
         */
        @KetherParser(["skill"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("cooldown") {
                    val operator = when (it.nextToken()) {
                        "add" -> Operator.ADD
                        "take" -> Operator.TAKE
                        "set" -> Operator.SET
                        "reset" -> Operator.RESET
                        else -> error("error of case")
                    }
                    CooldownOperator(operator, it.nextParsedAction(), it.tryGet(arrayOf("of", "the")))
                }
                case("level") {
                    actionSkillNow(tryGet(arrayOf("of","the"))) { it.level }
                }
                case("level-cap","max-level","level-max") {
                    actionSkillNow(tryGet(arrayOf("of","the"))) { it.maxLevel }
                }
                case("name") {
                    actionSkillNow(tryGet(arrayOf("of","the"))) { it.name }
                }
                other {
                    val varKey = nextToken()
                    actionSkillNow(tryGet(arrayOf("of","the"))) {
                        val variable = it.instance.option.variables.firstOrNull { it.key == varKey } ?: error("No variable ${varKey} define.")
                        val context = ContextAPI.create(bukkitPlayer()!!, it)
                        ScriptLoader.createScript(context,variable.expression)
                    }
                }

            }

        }

        fun actionSkillNow(of: ParsedAction<*>?,func: QuestContext.Frame.(PlayerJob.Skill) -> Any?) = actionNow {
            val future = CompletableFuture<Any?>()
            if (of != null) {
                run(of).str { skill ->
                    future.complete(func(this,this.bukkitPlayer()?.plannersProfile?.getSkill(skill) ?: error("No skill $skill")))
                }
            } else {
                future.complete(func(this,this.skill()))
            }
        }

        fun actionTake(of: ParsedAction<*>?, func: QuestContext.Frame.(PlayerJob.Skill) -> CompletableFuture<*>) = actionTake {
            val future = CompletableFuture<Any>()
            if (of != null) {
                run(of).str { skill ->
                    func(this,this.bukkitPlayer()?.plannersProfile?.getSkill(skill) ?: error("No skill $skill")).thenAccept {
                        future.complete(it)
                    }
                }
            } else {
                func(this,this.skill()).thenAccept {
                    future.complete(it)
                }
            }
            future
        }
        fun execute(player: Player, skill: Skill, operator: Operator, amount: Long) {
            when (operator) {
                Operator.ADD -> Counting.increase(player, skill, amount)
                Operator.TAKE -> Counting.reduce(player, skill, amount)
                Operator.SET -> Counting.set(player, skill, amount)
                Operator.RESET -> Counting.reset(player, Session(player.toTarget(), skill))
            }
        }

    }
}
