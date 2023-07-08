package com.bh.planners.api.compat.chemdah

import com.bh.planners.api.event.PlayerSelectedJobEvent
import com.bh.planners.util.isWorld
import ink.ptms.chemdah.core.quest.objective.ObjectiveCountableI
@LoadQuest("selector")
object PlayerSelectedJob : ObjectiveCountableI<PlayerSelectedJobEvent>() {

    override val name = "planners selected job"
    override val event = PlayerSelectedJobEvent::class.java

    override val isAsync = true

    init {
        handler {
            it.profile.player
        }
        addSimpleCondition("job") { data, it ->
            data.toString() == it.profile.job?.name
        }
        addSimpleCondition("level") { data, it ->
            data.toInt() <= it.profile.level
        }
        addSimpleCondition("world") { data, it ->
            it.profile.player.world.isWorld(data.toString())
        }
    }
}