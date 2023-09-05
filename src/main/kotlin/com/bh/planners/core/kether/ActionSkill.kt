package com.bh.planners.core.kether

import com.bh.planners.api.Counting
import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.common.Operator
import com.bh.planners.api.runVariable
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.ActionLazyVariable.runVariable
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper
import com.bh.planners.core.kether.common.KetherHelper.containerOrSender
import com.bh.planners.core.kether.common.MultipleKetherParser
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import org.bukkit.entity.Player

@CombinationKetherParser.Used
object ActionSkill : MultipleKetherParser("skill") {

    val cooldown = KetherHelper.simpleKetherParser<Unit> {
        it.group(text(), long(), command("of","at", then = text()).option().defaultsTo(null),containerOrSender()).apply(it) { operator,tick, skill, container ->
            now {
                val operator1 = Operator.valueOf(operator.uppercase())
                val instance = if (skill != null) {
                    PlannersAPI.getSkill(skill) ?: error("Skill $skill not found.")
                } else {
                    skill().instance
                }
                container.forEachPlayer {
                    executeCooldown(this,instance,operator1,tick)
                }
            }
        }
    }


    fun executeCooldown(player: Player, skill: Skill, operator: Operator, amount: Long) {
        when (operator) {
            Operator.ADD -> Counting.increase(player, skill, amount)
            Operator.TAKE -> Counting.reduce(player, skill, amount)
            Operator.SET -> Counting.set(player, skill, amount)
            Operator.RESET -> Counting.reset(player, Session(player.toTarget(), skill))
        }
    }

    val level = KetherHelper.simpleKetherParser<Int> {
        it.group(command("of","at", then = text()).option(),containerOrSender()).apply(it) { skill,container ->
            now {
                val instance = if (skill != null) {
                    PlannersAPI.getSkill(skill) ?: error("Skill $skill not found.")
                } else {
                    skill().instance
                }
                val bukkitPlayer = container.firstBukkitPlayer()
                bukkitPlayer?.plannersProfile?.getSkillOrNull(instance)?.level ?: -1
            }
        }
    }

    val variable = KetherHelper.simpleKetherParser<Any?>("var") {
        it.group(text(), command("level", then = int()).option().defaultsTo(1),command("of","at", then = text()).option(),containerOrSender()).apply(it) { id, level, skill, container ->
            now {
                val instance = if (skill != null) {
                    PlannersAPI.getSkill(skill) ?: error("Skill $skill not found.")
                } else {
                    skill().instance
                }
                if (instance == skill().instance) {
                    this.runVariable(id)
                } else {
                    val bukkitPlayer = container.firstBukkitPlayer()
                    instance.runVariable(bukkitPlayer ?: return@now null,level,id).getNow(null)
                }
            }
        }
    }
}
