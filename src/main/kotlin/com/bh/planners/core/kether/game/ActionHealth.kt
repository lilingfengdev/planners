package com.bh.planners.core.kether.game

import com.bh.planners.api.common.Operator
import com.bh.planners.core.kether.*
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionHealth {

    class HealthOperation(val mode: Operator, val value: ParsedAction<*>, val selector: ParsedAction<*>?) :
        ScriptAction<Void>() {

        fun execute(entity: LivingEntity, value: Double) {
            val result = when (mode) {
                Operator.ADD -> entity.health + value
                Operator.TAKE -> entity.health - value
                Operator.SET -> value
            }.coerceAtLeast(0.0).coerceAtMost(entity.maxHealth)
            entity.health = result
        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(value).run<Any>().thenAccept {
                val value = Coerce.toDouble(it)
                catchRunning {
                    if (selector != null) {
                        frame.execLivingEntity(selector) { execute(this, value) }
                    } else {
                        execute(frame.asPlayer() ?: return@catchRunning, value)
                    }
                }
            }
        }


    }

    companion object {

        /**
         * health add 10 <they "-@range 3">
         * health take 10 <they "-@range 3">
         * health set 10 <they "-@range 3">
         */
        @KetherParser(["health"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            val operator = when (it.nextToken()) {
                "add" -> Operator.ADD
                "set" -> Operator.SET
                "take" -> Operator.TAKE
                else -> error("error of case!")
            }
            HealthOperation(operator, it.next(ArgTypes.ACTION), it.selectorAction())
        }

    }


}