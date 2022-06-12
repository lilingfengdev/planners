package com.bh.planners.core.kether.effect

import com.bh.planners.api.particle.EffectOption
import com.bh.planners.core.kether.effect.renderer.EffectRenderer
import com.bh.planners.core.skill.inline.Capture
import com.bh.planners.core.kether.getSession
import com.bh.planners.core.kether.toOriginLocation
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.skill.inline.InlineEvent.Companion.callEvent
import org.bukkit.entity.LivingEntity
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

abstract class Effect(val action: ParsedAction<*>) : ScriptAction<Void>() {

    companion object {

        val EFFECT_AIR = object : EffectRenderer {
            override fun sendTo() = emptySet<LivingEntity>()
        }

    }

    val EffectOption.onCapture: String?
        get() = demand.get("onCapture")

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(action).run<String>().thenAccept {
            val session = frame.getSession()
            val effectOption = EffectOption(it)
            val set = handler(frame.toOriginLocation(), effectOption, session).sendTo()
            if (set.isNotEmpty() && effectOption.onCapture != null) {
                set.forEach { entity ->
                    session.callEvent(effectOption.onCapture!!, Capture(entity))
                }
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    abstract fun handler(target: Target?, option: EffectOption, session: Session): EffectRenderer

}
