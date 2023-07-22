package com.bh.planners.core.kether

import com.bh.planners.api.*
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.common.Operator
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.ActionLazyVariable.Companion.runVariable
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.player.PlayerJob
import org.bukkit.entity.Player
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionSkill {

    class CooldownOperator(val operator: Operator, val amount: ParsedAction<*>, val target: ParsedAction<*>?) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.readAccept<Long>(amount) { amount ->
                if (target != null) {
                    frame.readAccept<String>(target) last@{
                        val skill = PlannersAPI.getSkill(it) ?: return@last
                        execute(frame.bukkitPlayer() ?: return@last, skill, operator, amount * 50)
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

    class ActionSkillVariable(val id: ParsedAction<*>, val level: ParsedAction<*>, val skill: ParsedAction<*>?) :
        ScriptAction<Any?>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Any?> {
            val player = frame.bukkitPlayer() ?: return CompletableFuture.completedFuture(null)
            val future = CompletableFuture<Any?>()
            frame.run(id).str { id ->
                frame.run(level).int { level ->
                    if (skill == null) {
                        // 自动指定当前作用域技能
                        future.complete(frame.runVariable(id))
                    }
                    // 指定技能
                    else {
                        frame.run(skill).str { skillId ->
                            var skill = player.plannersProfile.getSkill(skillId)
                            if (skill == null) {
                                skill = PlayerJob.Skill(-1, skillId, level, null)
                            }
                            skill.runVariable(player, id).thenAccept { future.complete(it) }
                        }
                    }

                }
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
                    CooldownOperator(operator, it.nextParsedAction(), it.nextArgumentAction(arrayOf("of", "the")))
                }
                case("level") {
                    actionSkillNow(nextArgumentAction(arrayOf("of", "the"))) { it.level }
                }
                case("level-cap", "max-level", "level-max") {
                    actionSkillNow(nextArgumentAction(arrayOf("of", "the"))) { it.maxLevel }
                }
                case("name") {
                    actionSkillNow(nextArgumentAction(arrayOf("of", "the"))) { it.name }
                }
                case("variable", "var") {
                    ActionSkillVariable(
                        it.nextParsedAction(),
                        it.nextArgumentAction(arrayOf("level"), 0)!!,
                        it.nextArgumentActionOrNull(arrayOf("of"))
                    )
                }
                other {
                    val varKey = nextToken()
                    actionSkillNow(nextArgumentAction(arrayOf("of", "the"))) {
                        val variable = it.instance.option.variables.firstOrNull { it.key == varKey }
                            ?: error("No variable $varKey define.")
                        val context = ContextAPI.create(bukkitTarget(), it.instance)
                        ScriptLoader.createScript(context, variable.expression)
                    }
                }

            }

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
