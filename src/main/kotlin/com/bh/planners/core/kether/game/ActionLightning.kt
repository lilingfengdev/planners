package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.*
import com.bh.planners.core.effect.Target
import org.bukkit.Location
import org.bukkit.Particle
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionLightning(val selector: ParsedAction<*>?) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        if (selector != null) {
            frame.exec(selector) {
                val loc = when (this) {
                    is Target.Entity -> {
                        this.proxy.location
                    }
                    is Target.Location -> {
                        this.value
                    }
                    else -> return@exec
                }
                lightning(loc)
            }
        } else {
            lightning(frame.origin()?.value ?: return CompletableFuture.completedFuture(null))
        }

        return CompletableFuture.completedFuture(null)
    }

    private fun lightning(loc: Location) {
        loc.world!!.strikeLightningEffect(loc)
    }

    companion object {

        /**
         * 在指定(目标)坐标处召唤一条无伤闪电
         * lightning [selector]
         */
        @KetherParser(["lightning"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionLightning(it.nextSelector())
        }
    }
}