package com.bh.planners.core.kether.effect

import com.bh.planners.api.particle.EffectOption
import com.bh.planners.core.kether.getSession
import com.bh.planners.core.pojo.Session
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.library.kether.ParsedAction
import taboolib.module.effect.ParticleObj
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.script
import java.util.concurrent.CompletableFuture

abstract class Effect(val action: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(action).run<String>().thenAccept {
            try {
                sendTo(frame.script().sender!!.cast(), EffectOption(it), frame.getSession())
                    .apply { info(this) }.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    abstract fun sendTo(sender: Player, option: EffectOption, session: Session): ParticleObj

}
