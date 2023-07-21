package com.bh.planners.core.kether

import com.bh.planners.api.PlannersAPI.plannersProfile
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionShutDown(val selector: ParsedAction<*>?) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.containerOrSender(selector).thenAccept {
            it.forEachPlayer {
                plannersProfile.runningScripts.map { it.value.let { script -> script.service.terminateQuest(script) } }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        /**
         * 立即停止玩家正在运行的所有技能
         * shutdown [selector]
         */
        @KetherParser(["shutdown"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionShutDown(it.nextSelectorOrNull())
        }
    }

}