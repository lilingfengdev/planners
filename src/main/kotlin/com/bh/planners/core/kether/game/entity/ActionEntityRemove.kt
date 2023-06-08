package com.bh.planners.core.kether.game.entity

import com.bh.planners.core.kether.execEntity
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionEntityRemove(
    val selector: ParsedAction<*>,
) : ScriptAction<List<Entity>>() {

    override fun run(frame: ScriptFrame): CompletableFuture<List<Entity>> {
        frame.execEntity(selector) {
            if (this is LivingEntity) {
                this.remove()
            }
        }
        return CompletableFuture.completedFuture(null)
    }

}