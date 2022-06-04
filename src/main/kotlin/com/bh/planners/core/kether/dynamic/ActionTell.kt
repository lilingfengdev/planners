package com.bh.planners.core.kether.dynamic

import com.bh.planners.api.particle.Demand
import com.bh.planners.core.kether.effect.Target.Companion.createContainer
import com.bh.planners.core.kether.getSession
import com.bh.planners.core.kether.toOriginLocation
import org.bukkit.entity.Player
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionTell(val message: ParsedAction<*>, val selector: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.newFrame(message).run<Any?>().thenAccept { message ->
            frame.newFrame(selector).run<String>().thenAccept { selector ->
                Demand(selector).createContainer(frame.toOriginLocation(),frame.getSession()).forEachEntity {
                    if (this is Player) {
                        sendMessage(message.toString().trimIndent())
                    }
                }
            }
        }
    }

    internal object Parser {

        /**
         * 给selector内玩家发送message消息
         * <tell/send/message> [message] [selector]
         */
        @KetherParser(["tell", "send", "message"])
        fun parser() = scriptParser {
            ActionTell(it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION))
        }
    }
}