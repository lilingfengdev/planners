package com.bh.planners.core.kether.compat.dragoncore

import com.bh.planners.core.kether.execLivingEntity
import eos.moe.dragoncore.api.CoreAPI
import org.bukkit.entity.LivingEntity
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionDragonAnimation(
    val state: String,
    val remove: Boolean,
    val transition: ParsedAction<*>,
    val selector: ParsedAction<*>,
) : ScriptAction<Void>() {

    fun execute(entity: LivingEntity, state: String, remove: Boolean, transitionTime: Int) {
        if (remove) {
            CoreAPI.removeEntityAnimation(entity, state, transitionTime)
        } else {
            CoreAPI.setEntityAnimation(entity, state, transitionTime)
        }
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        frame.newFrame(transition).run<Any>().thenAccept {
            val transition = Coerce.toInteger(it)
            frame.execLivingEntity(selector) { execute(this, state, remove, transition) }
        }
        return CompletableFuture.completedFuture(null)
    }
}