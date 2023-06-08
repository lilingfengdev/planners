package com.bh.planners.core.kether.event

import com.bh.planners.api.common.Operator
import com.bh.planners.api.common.Operator.*
import com.bh.planners.api.event.proxy.ProxyDamageEvent
import com.bh.planners.core.kether.ActionEvent.Companion.event
import com.bh.planners.core.kether.eventParser
import com.bh.planners.core.kether.readAccept
import org.bukkit.event.Cancellable
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.expects
import java.util.concurrent.CompletableFuture

/**
 * 事件取消
 * event cancel [to [false/true]]
 */
class ActionEventDamage(val action: ParsedAction<*>) : ScriptAction<Boolean>() {


    class ActionEventDamageGet : ScriptAction<Double>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Double> {
            val event = frame.event()
            if (event is ProxyDamageEvent) {
                return CompletableFuture.completedFuture(event.realDamage)
            }
            error("Error running environment !")
        }

    }

    class ActionEventDamageOperator(val action: ParsedAction<*>, val operator: Operator) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val event = frame.event()
            val future = CompletableFuture<Void>()
            if (event is ProxyDamageEvent) {
                frame.readAccept<Double>(action) { value ->
                    when (operator) {
                        ADD -> event.addDamage(value)
                        SET -> event.damage = value
                        TAKE -> event.addDamage(-value)
                        RESET -> event.damage = 0.0
                    }
                    future.complete(null)
                }
            }
            return future
        }

    }

    override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
        val event = frame.event()
        if (event is Cancellable) {
            return frame.newFrame(action).run<Any>().thenApply {
                event.isCancelled = Coerce.toBoolean(it)
                event.isCancelled
            }
        }
        return CompletableFuture.completedFuture(false)
    }

    companion object {

        /**
         * event damage +=/add 10
         * event damage -=/take 10
         * event damage =/set 10
         * event damage
         */
        @KetherParser(["damage"])
        fun parser() = eventParser {

            try {
                it.mark()
                when (it.expects("add", "+=", "take", "-=", "set", "=", "to", "finalDamage", "finaldamage")) {
                    "add", "+=" -> ActionEventDamageOperator(it.nextParsedAction(), ADD)
                    "take", "-=" -> ActionEventDamageOperator(it.nextParsedAction(), TAKE)
                    "set", "=" -> ActionEventDamageOperator(it.nextParsedAction(), SET)
                    else -> error("error")
                }
            } catch (_: Exception) {
                it.reset()
                ActionEventDamageGet()
            }

        }

    }


}