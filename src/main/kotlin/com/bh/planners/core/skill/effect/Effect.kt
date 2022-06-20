package com.bh.planners.core.skill.effect

import com.bh.planners.core.skill.effect.renderer.EffectRenderer
import com.bh.planners.core.kether.getSession
import com.bh.planners.core.kether.toOriginLocation
import com.bh.planners.core.pojo.Session
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

abstract class Effect(val action: ParsedAction<*>) : ScriptAction<Void>() {

    companion object {

        val EFFECT_AIR = object : EffectRenderer {
            override fun sendTo() {}
        }

    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(action).run<String>().thenAccept {
            try {
                val session = frame.getSession()
                val effectOption = EffectOption(it)
                handler(frame.toOriginLocation(), effectOption, session).sendTo()
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    abstract fun handler(target: Target?, option: EffectOption, session: Session): EffectRenderer

}
