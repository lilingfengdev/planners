package com.bh.planners.api.compat.chemdah

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.event.PlayerCastSkillEvents
import com.bh.planners.util.isWorld
import ink.ptms.chemdah.core.quest.objective.ObjectiveCountableI

object PostSkillCast:ObjectiveCountableI<PlayerCastSkillEvents.Post>() {

    override val name = "post skill cast"
    override val event = PlayerCastSkillEvents.Post::class.java

    override val isAsync = true

    init {
        handler {
            it.player
        }
        addSimpleCondition("skill") { data, e ->
            data.toString() == e.skill.option.name
        }
        addSimpleCondition("level") { data, e ->
            e.player.plannersProfile.job!!.level >= data.toInt()
        }
        addSimpleCondition("world") { data, e ->
            e.player.world.isWorld(data.toString())
        }
        addSimpleCondition("job") { data, event ->
            event.player.plannersProfile.job?.name == data.toString()
        }
    }
}