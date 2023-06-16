package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.containerOrSender
import com.bh.planners.core.kether.createContainer
import com.germ.germplugin.api.GermPacketAPI
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.long
import taboolib.module.kether.run
import java.util.concurrent.CompletableFuture

class ActionGermLook(
    val duration: ParsedAction<*>,
    val group: ParsedAction<*>,
    val target: ParsedAction<*>?,
) : ScriptAction<Void>() {

    fun execute(duration: Long, group: Target.Container, target: Target.Container) {
        val entityId = target.firstEntityTarget()?.entityId ?: return
        group.forEachPlayer {
            GermPacketAPI.sendLockPlayerCameraFaceEntity(this, entityId, duration)
        }
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.run(duration).long { duration ->
            frame.createContainer(group).thenAccept { container ->
                frame.containerOrSender(target).thenAccept { target ->
                    execute(duration, container, target)
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

}