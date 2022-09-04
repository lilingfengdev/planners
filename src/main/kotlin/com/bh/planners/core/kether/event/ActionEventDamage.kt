package com.bh.planners.core.kether.event

import ac.github.oa.api.event.entity.OriginCustomDamageEvent
import com.bh.planners.api.common.Operator
import com.bh.planners.api.event.proxy.ProxyDamageEvent
import com.bh.planners.core.kether.ActionEvent.Companion.event
import com.bh.planners.core.kether.eventParser
import com.bh.planners.core.kether.runTransfer0
import org.bukkit.Bukkit
import org.bukkit.event.Cancellable
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.actions.LiteralAction
import taboolib.module.kether.*
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
                return CompletableFuture.completedFuture(event.damage)
            }
            error("Error running environment !")
        }

    }

    class ActionEventDamageOperator(val action: ParsedAction<*>, val operator: Operator) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val event = frame.event()
            val future = CompletableFuture<Void>()
            if (event is ProxyDamageEvent) {
                frame.runTransfer0<Double>(action) { value ->
                    when (operator) {
                        Operator.ADD -> event.addDamage(value)
                        Operator.SET -> event.damage = value
                        Operator.TAKE -> event.addDamage(-value)
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
                    "add", "+=" -> ActionEventDamageOperator(it.next(ArgTypes.ACTION), Operator.ADD)
                    "take", "-=" -> ActionEventDamageOperator(it.next(ArgTypes.ACTION), Operator.TAKE)
                    "set", "=" -> ActionEventDamageOperator(it.next(ArgTypes.ACTION), Operator.SET)
                    else -> error("error")
                }
            } catch (_: Exception) {
                it.reset()
                ActionEventDamageGet()
            }

        }

    }


}