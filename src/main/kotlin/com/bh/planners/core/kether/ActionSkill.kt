package com.bh.planners.core.kether

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.common.Operator
import com.bh.planners.api.Counting
import com.bh.planners.core.pojo.Skill
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionSkill {

    class CooldownOperator(val operator: Operator, val amount: ParsedAction<*>, val target: ParsedAction<*>?) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.runTransfer0<Long>(amount) { amount ->
                if (target != null) {
                    frame.runTransfer0<String>(target) {
                        val skill = PlannersAPI.getSkill(it) ?: return@runTransfer0
                        execute(frame.asPlayer() ?: return@runTransfer0, skill, operator, amount * 50)
                    }
                } else {
                    execute(frame.asPlayer() ?: return@runTransfer0, frame.skill().instance, operator, amount * 50)
                }
            }

            return CompletableFuture.completedFuture(null)
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
                        else -> error("error of case")
                    }
                    CooldownOperator(operator, it.nextParsedAction(), it.tryGet(arrayOf("of", "the")))
                }

            }

        }

        fun execute(player: Player, skill: Skill, operator: Operator, amount: Long) {
            when (operator) {
                Operator.ADD -> Counting.increase(player, skill, amount)
                Operator.TAKE -> Counting.reduce(player, skill, amount)
                Operator.SET -> Counting.set(player, skill, amount)
            }
        }

    }
}
