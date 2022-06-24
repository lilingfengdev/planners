package com.bh.planners.core.kether.meta

import com.bh.planners.core.kether.rootVariables
import com.bh.planners.core.kether.toLocation
import com.bh.planners.core.kether.toOriginLocation
import org.bukkit.Location
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionMetaOrigin {


    class Set(val action: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.newFrame(action).run<String>().thenAccept {
                frame.rootVariables()["@Origin"] = it.toLocation()
            }
            return CompletableFuture.completedFuture(null)
        }

    }


    class Get : ScriptAction<Location>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Location> {
            return CompletableFuture.completedFuture(frame.toOriginLocation()?.value ?: ZERO)
        }

    }

    companion object {
        val ZERO = Location(null, 0.0, 0.0, 0.0)
    }
}