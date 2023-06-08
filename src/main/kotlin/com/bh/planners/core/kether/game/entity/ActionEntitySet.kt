package com.bh.planners.core.kether.game.entity

import com.bh.planners.core.kether.execEntity
import com.bh.planners.core.kether.runAny
import org.bukkit.entity.Entity
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionEntitySet(
    val yaw: ParsedAction<*>,
    val pitch: ParsedAction<*>,
    val selector: ParsedAction<*>,
) : ScriptAction<List<Entity>>() {

    override fun run(frame: ScriptFrame): CompletableFuture<List<Entity>> {
        frame.runAny(yaw) {
            val yaw = Coerce.toFloat(this)
            frame.runAny(pitch) {
                val pitch = Coerce.toFloat(this)
                frame.execEntity(selector) {
                    this.location.yaw = yaw
                    this.location.pitch = pitch
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

}