package com.bh.planners.api.compat.chemdah

import com.bh.planners.api.event.PlayerSkillUpgradeEvent
import ink.ptms.chemdah.core.quest.objective.Dependency
import ink.ptms.chemdah.core.quest.objective.ObjectiveCountableI

@Dependency("Planners")
object PlayerSkillUpgrade : ObjectiveCountableI<PlayerSkillUpgradeEvent>() {

    override val name = "planners skill upgrade"
    override val event = PlayerSkillUpgradeEvent::class.java

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
    }
}