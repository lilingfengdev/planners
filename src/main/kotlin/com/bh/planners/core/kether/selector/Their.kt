package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.asPlayer
import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.pojo.Session

object Their : Selector {
    override val names: Array<String>
        get() = arrayOf("their", "filterthis")

    override fun check(target: Target?, args: String, session: Session, container: Target.Container) {
        val player = session.executor.asPlayer() ?: return
        container.removeIf {
            if (this is Target.Entity) {
                livingEntity.entityId == player.entityId
            } else false
        }
    }
}