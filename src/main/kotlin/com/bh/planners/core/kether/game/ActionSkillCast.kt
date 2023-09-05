package com.bh.planners.core.kether.game

import com.bh.planners.api.PlannersAPI
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper
import com.bh.planners.core.kether.common.KetherHelper.containerOrSender

@CombinationKetherParser.Used
fun trycast() = KetherHelper.simpleKetherParser<Unit>("cast", "try-cast", "cast-try`") {
    it.group(text(),containerOrSender()).apply(it) { skill,container ->
        now { container.forEachPlayer { PlannersAPI.cast(this,skill) } }
    }
}

@CombinationKetherParser.Used
fun directcast() = KetherHelper.simpleKetherParser<Unit>("cast", "direct-cast") {
    it.group(text(), command("level", then = int()).option().defaultsTo(1),containerOrSender()).apply(it) { skill,level, container ->
        now { container.forEachPlayer { PlannersAPI.directCast(this,skill,level) } }
    }
}