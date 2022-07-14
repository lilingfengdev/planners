package com.bh.planners.core.kether.meta

import com.bh.planners.core.kether.rootVariables
import com.bh.planners.core.kether.toLocation
import com.bh.planners.core.kether.toOriginLocation
import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.createContainer
import org.bukkit.Location
import taboolib.common.platform.function.info
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionMetaOrigin {


    class Set(val action: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.createContainer(action).thenAccept {
                val locationTarget = it.firstLocationTarget()
                frame.rootVariables()["@Origin"] = locationTarget
            }

            return CompletableFuture.completedFuture(null)
        }

    }


    class Get : ScriptAction<Target.Location?>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Target.Location?> {
            return CompletableFuture.completedFuture(frame.toOriginLocation())
        }

    }

    companion object {
        val ZERO = Location(null, 0.0, 0.0, 0.0)
    }
}