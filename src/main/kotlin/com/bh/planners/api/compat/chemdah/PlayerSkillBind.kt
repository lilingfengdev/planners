package com.bh.planners.api.compat.chemdah

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.event.PlayerSkillBindEvent
import com.bh.planners.util.isWorld
import ink.ptms.chemdah.core.quest.objective.ObjectiveCountableI
@LoadQuest("skillBind")
object PlayerSkillBind : ObjectiveCountableI<PlayerSkillBindEvent>() {

    override val name = "planners skill bind"
    override val event = PlayerSkillBindEvent::class.java

    override val isAsync = true

    init {
        handler {
            it.player
        }
        addSimpleCondition("skill") { data, it ->
            data.toString() == it.skill.name
        }
        addSimpleCondition("level") { data, it ->
            data.toInt() <= it.skill.level
        }
        addSimpleCondition("slot") { data, it ->
            data.toString() == it.skill.keySlot?.key
        }
        addSimpleCondition("world") { data, it ->
            it.player.world.isWorld(data.toString())
        }
        addSimpleCondition("job") { data, it ->
            it.player.plannersProfile.job?.name == data.toString()
        }
    }
}