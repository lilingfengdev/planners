package com.bh.planners.core.kether.game

import com.bh.planners.api.ContextAPI
import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.bukkitPlayer
import com.bh.planners.core.kether.execPlayer
import com.bh.planners.core.kether.nextSelectorOrNull
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionSkillCast {

    class TryCast(val skill: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(skill).run<Any>().thenAccept { skill ->
                if (selector != null) {
                    frame.execPlayer(selector) {
                        PlannersAPI.cast(this, skill.toString(), true)
                    }
                } else {
                    PlannersAPI.cast(frame.bukkitPlayer() ?: return@thenAccept, skill.toString(), true)
                }
            }
        }
    }

    class DirectCast(val skill: ParsedAction<*>, val level: ParsedAction<*>, val selector: ParsedAction<*>?) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(skill).run<Any>().thenAccept {
                val skill = PlannersAPI.getSkill(it.toString()) ?: return@thenAccept
                frame.newFrame(level).run<Any>().thenAccept {
                    val level = Coerce.toInteger(it)
                    if (selector != null) {
                        frame.execPlayer(selector) {
                            ContextAPI.create(this, skill, level)?.cast()
                        }
                    } else {
                        val player = frame.bukkitPlayer() ?: return@thenAccept
                        ContextAPI.create(player, skill, level)?.cast()
                    }
                }
            }
        }
    }

    companion object {

        /**
         * 为目标玩家尝试释放技能
         *
         * 满足蓝量条件
         * 满足冷却条件
         * 满足拥有该技能条件
         *
         * try-cast "def0" they "@self"
         */
        @KetherParser(["try-cast"], namespace = NAMESPACE, shared = true)
        fun parser1() = scriptParser {
            TryCast(it.nextParsedAction(), it.nextSelectorOrNull())
        }

        /**
         * 为目标玩家释放技能
         * 不参与任何条件限制 直接指定等级释放
         *
         * direct-cast "def0" 1 they "@self"
         */
        @KetherParser(["direct-cast"], namespace = NAMESPACE, shared = true)
        fun parser2() = scriptParser {
            DirectCast(it.nextParsedAction(), it.nextParsedAction(), it.nextSelectorOrNull())
        }
    }

}