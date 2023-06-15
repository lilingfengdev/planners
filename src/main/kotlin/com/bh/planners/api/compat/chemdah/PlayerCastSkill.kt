package com.bh.planners.api.compat.chemdah

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.event.PlayerCastSkillEvents
import ink.ptms.chemdah.core.quest.objective.Dependency
import ink.ptms.chemdah.core.quest.objective.ObjectiveCountableI

@Dependency("Planners")
object PlayerCastSkill : ObjectiveCountableI<PlayerCastSkillEvents.Post>() {

    override val name = "planners cast skill"
    override val event = PlayerCastSkillEvents.Post::class.java

    override val isAsync = true

    init {
        handler {
            it.player
        }
        addSimpleCondition("name") { data, it ->
            data.toString() == it.skill.key
        }
        addSimpleCondition("level") { data, it ->
            (it.player.plannersProfile.getSkill(it.skill.key)?.level ?: 0) >= data.toInt()
        }
    }
}