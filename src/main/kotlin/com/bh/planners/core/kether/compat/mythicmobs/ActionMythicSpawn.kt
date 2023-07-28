package com.bh.planners.core.kether.compat.mythicmobs

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.compat.mythicmobs.ActionMythicMobsLoader.api
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.origin
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

/**
 *   package com.bh.planners.core.kether.compat.mythicmobs
 *   time 2023/7/28/15:55:38
 *   author 劫
 */

class ActionMythicSpawn(val mob: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Target.Container>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Target.Container> {
        val future = CompletableFuture<Target.Container>()
        frame.newFrame(mob).run<Any>().thenAccept { mob ->
            val container = Target.Container()
            if (selector != null) {
                frame.createContainer(selector).thenAccept { selector ->
                    selector.forEachLocation {
                        container += api.mobManager.spawnMob(mob.toString(), this).entity.bukkitEntity.toTarget()
                    }
                    future.complete(container)
                }
            } else {
                container += api.mobManager.spawnMob(
                    mob.toString(),
                    frame.origin().value
                ).entity.bukkitEntity.toTarget()
                future.complete(container)
            }

        }
        return future
    }
}
