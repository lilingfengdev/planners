package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.bukkitPlayer
import com.bh.planners.core.kether.execPlayer
import com.bh.planners.core.kether.nextSelectorOrNull
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionTell(val message: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.newFrame(message).run<Any?>().thenAccept {
            val message = it?.toString() ?: "null"
            if (selector != null) {
                frame.execPlayer(selector) { sendMessage(message.trimIndent()) }
            } else {
                frame.bukkitPlayer()?.sendMessage(message.trimIndent())
            }

        }
    }

    internal object Parser {

        /**
         * 给selector内玩家发送message消息
         * <tell/send/message> [message] [selector]
         */
        @KetherParser(["tell", "send", "message"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionTell(it.nextParsedAction(), it.nextSelectorOrNull())
        }
    }
}