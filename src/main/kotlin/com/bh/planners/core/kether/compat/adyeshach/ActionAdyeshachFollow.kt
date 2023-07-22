package com.bh.planners.core.kether.compat.adyeshach

import com.bh.planners.core.kether.compat.adyeshach.ActionAdyeshach.execAdyeshachEntity
import com.bh.planners.core.kether.createContainer
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionAdyeshachFollow(val owner: ParsedAction<*>, val selector: ParsedAction<*>, val option: ParsedAction<*>) :
    ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        return frame.createContainer(owner).thenAccept { it ->
            val entityTarget = it.firstProxyEntity() ?: return@thenAccept
            frame.newFrame(option).run<Any>().thenAccept {
                val option = it.toString()
                frame.execAdyeshachEntity(selector) {
                    EntityFollow.select(entityTarget, instance, option)
                }
            }

        }
    }

}