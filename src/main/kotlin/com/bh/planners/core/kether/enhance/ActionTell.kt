package com.bh.planners.core.kether.enhance

import com.bh.planners.core.kether.*
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionTell(val message: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.newFrame(message).run<Any?>().thenAccept { message ->
            if (selector != null) {
                frame.execPlayer(selector) { sendMessage(message.toString().trimIndent()) }
            } else {
                frame.asPlayer()?.sendMessage(message.toString().trimIndent())
            }

        }
    }

    internal object Parser {

        /**
         * 给selector内玩家发送message消息
         * <tell/send/message> [message] [selector]
         */
        @KetherParser(["tell", "send", "message"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            ActionTell(it.next(ArgTypes.ACTION), it.selectorAction())
        }
    }
}