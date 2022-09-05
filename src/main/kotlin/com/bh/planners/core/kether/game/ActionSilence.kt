package com.bh.planners.core.kether.game

import com.bh.planners.api.event.PlayerCastSkillEvent
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.execPlayer
import com.bh.planners.core.kether.selectorAction
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionSilence(
    val seconds: ParsedAction<*>,
    val selector: ParsedAction<*>?
) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(seconds).run<Any>().thenApply {
            frame.execPlayer(selector!!) {
                silenceMap[uniqueId] = (System.currentTimeMillis() + Coerce.toDouble(it) * 1000).toLong()
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        private val silenceMap = mutableMapOf<UUID, Long>()
        /**
         * 沉默目标 使对方在一定时间内无法释放技能
         *  *** 暂时无效 等PlayerCastSkillEvent完善即可
         * silence [seconds] [selector]
         */
        @KetherParser(["silence"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val seconds = it.nextParsedAction()
            ActionSilence(seconds, it.selectorAction())
        }

        @SubscribeEvent(EventPriority.LOWEST)
        fun onCastSkill(e: PlayerCastSkillEvent.Pre) {
            val time = silenceMap[e.player.uniqueId] ?: return
            if (System.currentTimeMillis() > time) {
                silenceMap.remove(e.player.uniqueId)
            } else {
                e.isCancelled = true
            }
        }
    }
}