package com.bh.planners.core.kether.meta

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.getContext
import com.bh.planners.core.kether.origin
import org.bukkit.Location
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionMetaOrigin {


    class Set(val action: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            return frame.createContainer(action).thenAccept {
                val locationTarget = it.firstTarget()
                if (locationTarget != null) {
                    frame.getContext().origin = locationTarget
                }
            }
        }

    }


    class Get : ScriptAction<Target.Location?>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Target.Location?> {
            return CompletableFuture.completedFuture(frame.origin())
        }

    }

    companion object {
        val ZERO = Location(null, 0.0, 0.0, 0.0)
    }
}