package com.bh.planners.api.compat.chemdah

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.event.PlayerCastSkillEvents
import com.bh.planners.util.isWorld
import ink.ptms.chemdah.core.quest.objective.ObjectiveCountableI

object PreSkillCast : ObjectiveCountableI<PlayerCastSkillEvents.Pre>() {
    override val event: Class<PlayerCastSkillEvents.Pre> = PlayerCastSkillEvents.Pre::class.java
    override val name: String = "pre cast skill"

    init {
        handler {
            it.player
        }
        addSimpleCondition("skill") { data, e ->
            data.toString() == e.skill.key
        }
        addSimpleCondition("level") { data, event ->
            event.player.plannersProfile.job!!.level >= data.toInt()
        }
        addSimpleCondition("world") { data, event ->
            event.player.world.isWorld(data.toString())
        }
        addSimpleCondition("job") { data, event ->
            event.player.plannersProfile.job?.name == data.toString()
        }
    }
}