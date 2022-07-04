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
            return frame.newFrame(amount).run<Any>().thenAccept { amount ->
                if (target != null) {
                    frame.newFrame(target).run<Any>().thenAccept { s ->
                        val skill = PlannersAPI.getSkill(s.toString()) ?: return@thenAccept
                        execute(frame.asPlayer() ?: return@thenAccept, skill, operator, Coerce.toLong(amount))
                    }
                } else {
                    execute(
                        frame.asPlayer() ?: return@thenAccept,
                        frame.skill().instance,
                        operator,
                        Coerce.toLong(amount)
                    )
                }
            }
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
        @KetherParser(["skill"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            it.switch {
                case("cooldown") {
                    val operator = when (it.nextToken()) {
                        "add" -> Operator.ADD
                        "take" -> Operator.TAKE
                        "set" -> Operator.SET
                        else -> error("error of case")
                    }
                    val amount = it.next(ArgTypes.ACTION)
                    val of = try {
                        mark()
                        expects("of", "the")
                        it.next(ArgTypes.ACTION)
                    } catch (_: Exception) {
                        reset()
                        null
                    }
                    CooldownOperator(operator, amount, of)
                }

            }

        }

        fun execute(player: Player, skill: Skill, operator: Operator, amount: Long) {
            when (operator) {
                Operator.ADD -> Counting.increase(player, skill, amount)
                Operator.TAKE -> Counting.reduce(player, skill, amount)
                Operator.SET -> {}
            }
        }

    }
}
