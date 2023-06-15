package com.bh.planners.core.kether.game

import com.bh.planners.api.event.PlayerCastSkillEvents
import com.bh.planners.api.event.PlayerSilenceEvent
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.execPlayer
import com.bh.planners.core.kether.nextArgumentAction
import com.bh.planners.core.kether.nextSelectorOrNull
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionSilence(
    val callevent: ParsedAction<*>,
    val ticks: ParsedAction<*>,
    val selector: ParsedAction<*>?,
) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(callevent).run<Boolean>().thenAccept { callevent ->
            frame.newFrame(ticks).run<Any>().thenApply { ticks ->
                frame.execPlayer(selector!!) {
                    silenceMap[uniqueId] = (System.currentTimeMillis() + Coerce.toDouble(ticks) * 50).toLong()
                    if (callevent) {
                        PlayerSilenceEvent(this, (Coerce.toDouble(ticks) * 50).toLong()).call()
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        private val silenceMap = mutableMapOf<UUID, Long>()

        /**
         * 沉默目标 使对方在一定时间内无法释放技能
         *  *** 暂时无效 等PlayerCastSkillEvents完善即可
         * silence <callevent: false> [ticks] [selector]
         */
        @KetherParser(["silence"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val callevent = it.nextArgumentAction(arrayOf("callevent"), "false")!!
            val ticks = it.nextParsedAction()
            ActionSilence(callevent, ticks, it.nextSelectorOrNull())
        }

        @SubscribeEvent(EventPriority.LOWEST)
        fun onCastSkill(e: PlayerCastSkillEvents.Pre) {
            val time = silenceMap[e.player.uniqueId] ?: return
            if (System.currentTimeMillis() > time) {
                silenceMap.remove(e.player.uniqueId)
            } else {
                e.isCancelled = true
            }
        }
    }
}