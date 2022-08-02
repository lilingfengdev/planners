package com.bh.planners.core.kether.game

import com.bh.planners.api.common.SimpleTimeoutTask
import com.bh.planners.core.kether.*
import org.bukkit.entity.Entity
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionGlowing(val tick: ParsedAction<*>, val value: ParsedAction<*>, val selector: ParsedAction<*>?) :
    ScriptAction<Void>() {

    fun execute(entity: Entity, value: Boolean, tick: Long) {
        entity.isGlowing = value
        if (tick == -1L) return
        SimpleTimeoutTask.createSimpleTask(tick, true) {
            entity.isGlowing = !value
        }
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        frame.runTransfer0<Long>(tick) { tick ->
            frame.runTransfer0<Boolean>(value) { value ->
                if (selector != null) {
                    frame.execEntity(selector) {
                        execute(this, value, tick)
                    }
                } else {
                    execute(frame.asPlayer() ?: return@runTransfer0, value, tick)
                }
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    companion object {

        /**
         * 设置目标发光，-1为永久值 需要取消
         * glowing <timeout: action(-1)> <value: action(true)> <selector>
         */
        @KetherParser(["glowing"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionGlowing(
                it.tryGet(arrayOf("tick", "time", "timeout"), -1)!!,
                it.tryGet(arrayOf("value"), true)!!,
                it.selectorAction()
            )
        }

    }


}