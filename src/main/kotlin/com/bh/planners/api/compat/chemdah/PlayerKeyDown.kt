package com.bh.planners.api.compat.chemdah

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.event.PlayerKeydownEvent
import com.bh.planners.util.isWorld
import ink.ptms.chemdah.core.quest.objective.ObjectiveCountableI

@LoadQuest("keyDown")
object PlayerKeyDown : ObjectiveCountableI<PlayerKeydownEvent>() {

    override val event: Class<PlayerKeydownEvent> = PlayerKeydownEvent::class.java
    override val name: String = "key down"

    init {
        handler {
            it.player
        }
        addSimpleCondition("key") { data, it ->
            it.keySlot.key == data.toString()
        }
        addSimpleCondition("world") { data, it ->
            it.player.world.isWorld(data.toString())
        }
        addSimpleCondition("job") { data, it ->
            it.player.plannersProfile.job?.name == data.toString()
        }
        addSimpleCondition("level") { data, it ->
            it.player.plannersProfile.level >= data.toInt()
        }
    }
}