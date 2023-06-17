package com.bh.planners.api.compat.chemdah

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.event.PlayerCastSkillEvents
import com.bh.planners.util.isWorld
import ink.ptms.chemdah.core.quest.objective.ObjectiveCountableI

object RecordSkillCast :ObjectiveCountableI<PlayerCastSkillEvents.Record>() {

    override val name = "record skill cast"
    override val event = PlayerCastSkillEvents.Record::class.java

    override val isAsync = true

    init {
        FailureSkillCast.handler {
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