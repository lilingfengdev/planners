package com.bh.planners.core.kether.compat.adyeshach

import com.bh.planners.core.kether.*
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionAdyeshachFollow(val owner: ParsedAction<*>, val selector: ParsedAction<*>, val option: ParsedAction<*>) :
    ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        return frame.createContainer(owner).thenAccept {
            val entityTarget = it.firstLivingEntityTarget() ?: return@thenAccept
            frame.newFrame(option).run<Any>().thenAccept {
                val option = it.toString()
                frame.execEntity(selector) {
                    if (this is AdyeshachEntity) {
                        EntityFollow.select(entityTarget, this.entity, option)
                    }
                }
            }

        }
    }

}