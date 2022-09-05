package com.bh.planners.core.kether.game

import com.bh.planners.api.common.SimpleTimeoutTask
import com.bh.planners.core.kether.*
import com.bh.planners.core.kether.util.GlowUtil
import org.bukkit.ChatColor
import org.bukkit.entity.Entity
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionGlowing(val tick: ParsedAction<*>, val value: ParsedAction<*>, val color: ParsedAction<*>, val selector: ParsedAction<*>?) :
    ScriptAction<Void>() {

    fun execute(entity: Entity, value: Boolean, color: ChatColor, tick: Long) {
        if (value) GlowUtil.setColor(entity, color) else GlowUtil.removeColor(entity)
        if (tick == -1L) return
        SimpleTimeoutTask.createSimpleTask(tick, true) {
            GlowUtil.removeColor(entity)
        }
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        frame.runTransfer0<Long>(tick) { tick ->
            frame.runTransfer0<Boolean>(value) { value ->
                frame.runTransfer0<ChatColor>(color) { color ->
                    val glowColor = Coerce.toEnum(color, ChatColor::class.java)
                    if (selector != null) {
                        frame.execEntity(selector) {
                            execute(this, value, glowColor, tick)
                        }
                    } else {
                        execute(frame.asPlayer() ?: return@runTransfer0, value, glowColor, tick)
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    companion object {

        /**
         * 设置目标发光，-1为永久值 需要取消
         * glowing <timeout: action(-1)> <value: action(true)> <selector>
         * glowing <timeout> <value> <color> <selector>
         */
        @KetherParser(["glowing"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionGlowing(
                it.tryGet(arrayOf("tick", "time", "timeout"), -1)!!,
                it.tryGet(arrayOf("value"), true)!!,
                it.tryGet(arrayOf("color"), ChatColor.WHITE)!!,
                it.selectorAction()
            )
        }

    }


}