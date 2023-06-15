package com.bh.planners.api.compat.chemdah

import com.bh.planners.api.event.PlayerSelectedJobEvent
import ink.ptms.chemdah.core.quest.objective.ObjectiveCountableI

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
    }
}