package com.bh.planners.core.kether.effect

import com.bh.planners.api.particle.EffectOption
import com.bh.planners.api.particle.EffectSpawner
import com.bh.planners.core.kether.getSession
import com.bh.planners.core.kether.toOriginLocation
import com.bh.planners.core.pojo.Session
import taboolib.library.kether.ParsedAction
import taboolib.module.effect.ParticleObj
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

abstract class Effect(val action: ParsedAction<*>) : ScriptAction<Void>() {

    companion object {

        val EFFECT_AIR = object : ParticleObj(EffectSpawner(EffectOption("EMPTY 0 0 0"))) {
            override fun show() {

            }
        }

    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(action).run<String>().thenAccept {
            try {
                sendTo(frame.toOriginLocation(), EffectOption(it), frame.getSession()).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    abstract fun sendTo(target: Target?, option: EffectOption, session: Session): ParticleObj

}
