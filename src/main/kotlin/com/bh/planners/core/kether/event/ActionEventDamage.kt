package com.bh.planners.core.kether.event

import com.bh.planners.api.common.Operator
import com.bh.planners.core.kether.ActionEvent.Companion.event
import com.bh.planners.core.kether.eventParser
import org.bukkit.event.Cancellable
import org.bukkit.event.entity.EntityDamageEvent
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
            if (event is EntityDamageEvent) {
                return CompletableFuture.completedFuture(event.damage)
            }
            error("Error running environment !")
        }

    }

    class ActionEventDamageOperator(val action: ParsedAction<*>, val operator: Operator) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val event = frame.event()
            if (event is EntityDamageEvent) {
                return frame.newFrame(action).run<Any>().thenAccept {
                    when (operator) {
                        Operator.ADD -> event.damage += Coerce.toDouble(it)
                        Operator.SET -> event.damage = Coerce.toDouble(it)
                        Operator.TAKE -> event.damage -= Coerce.toDouble(it)
                    }
                }
            }
            return CompletableFuture.completedFuture(null)
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
                when (it.expects("add", "+=", "take", "-=", "set", "=", "to")) {
                    "add", "+=" -> ActionEventDamageOperator(it.next(ArgTypes.ACTION), Operator.ADD)
                    "take", "-=" -> ActionEventDamageOperator(it.next(ArgTypes.ACTION), Operator.TAKE)
                    "set", "=" -> ActionEventDamageOperator(it.next(ArgTypes.ACTION), Operator.SET)
                    else -> error("error")
                }
            } catch (_: Exception) {
                ActionEventDamageGet()
            }

        }

    }


}