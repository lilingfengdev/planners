package com.bh.planners.core.kether.game

import com.bh.planners.api.common.SimpleUniqueTask
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.containerOrSender
import com.bh.planners.core.kether.nextOptionalParsedAction
import com.bh.planners.core.kether.nextSelectorOrNull
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.inventivetalent.glow.GlowAPI
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionGlowing(
    val tick: ParsedAction<*>,
    val value: ParsedAction<*>,
    val color: ParsedAction<*>,
    val selector: ParsedAction<*>?
) : ScriptAction<Void>() {

    fun execute(entity: Entity, value: Boolean, color: String, tick: Long) {
        if (value) {
            setColor(entity, color)
            if (tick != -1L) {
                SimpleUniqueTask.submit("@glowing:${entity.uniqueId}", tick) {
                    unsetColor(entity)
                }
            }
        } else {
            unsetColor(entity)
        }
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        frame.run(tick).long { tick ->
            frame.run(value).bool { glowing ->
                frame.run(color).str { color ->
                    frame.containerOrSender(selector).thenAccept {
                        it.forEachEntity {
                            execute(this, glowing, color, tick)
                        }
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        private val enabled: Boolean
            get() = Bukkit.getPluginManager().isPluginEnabled("GlowAPI")

        fun setColor(entity: Entity, color: String) {
            if (enabled) {
                val var1 = GlowAPI.Color.valueOf(color.uppercase(Locale.getDefault()))
                Bukkit.getOnlinePlayers().forEach {
                    GlowAPI.setGlowing(entity, var1, it)
                }
            } else {
                entity.isGlowing = true
            }
        }

        fun unsetColor(entity: Entity) {
            if (enabled) {
                Bukkit.getOnlinePlayers().forEach {
                    GlowAPI.setGlowing(entity, false, it)
                }
            } else {
                entity.isGlowing = false
            }
        }

        /**
         * 设置目标发光，-1为永久值 需要取消
         * glowing <timeout: action(-1)> <value: action(true)> <selector>
         * glowing <timeout> <value> <color> <selector>
         */
        @KetherParser(["glowing"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionGlowing(
                it.nextOptionalParsedAction(arrayOf("tick", "time", "timeout"), "-1")!!,
                it.nextOptionalParsedAction(arrayOf("value"), "true")!!,
                it.nextOptionalParsedAction(arrayOf("color"), "WHITE")!!,
                it.nextSelectorOrNull()
            )
        }

    }


}