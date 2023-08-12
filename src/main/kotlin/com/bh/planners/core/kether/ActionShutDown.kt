package com.bh.planners.core.kether

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.containerOrSender
import com.bh.planners.core.kether.common.simpleKetherParser
@CombinationKetherParser.Used
fun shutdown() = simpleKetherParser<Unit>("shutdown") {
    it.group(containerOrSender()).apply(it) { container ->
        now {
            container.forEachPlayer {
                // to list 防止侵入式污染
                plannersProfile.runningScripts.values.toList().forEach { script ->
                    script.service.terminateQuest(script)
                }
            }
        }
    }

}