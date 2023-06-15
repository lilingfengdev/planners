package com.bh.planners.api.compat.chemdah

import com.bh.planners.api.event.PlayerTransferEvent
import ink.ptms.chemdah.core.quest.objective.ObjectiveCountableI

object PlayerTransfer : ObjectiveCountableI<PlayerTransferEvent>() {

    override val name = "planners transfer"
    override val event = PlayerTransferEvent::class.java

    override val isAsync = true

    init {
        handler {
            it.player
        }
        addSimpleCondition("name") { data, it ->
            data.toString() == it.target.config.name
        }
    }
}