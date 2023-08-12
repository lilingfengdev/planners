package com.bh.planners.api

import com.bh.planners.api.PlannersAPI.plannersProfile
import org.bukkit.event.entity.PlayerDeathEvent
import taboolib.common.platform.event.SubscribeEvent

object PlayerState {

    val stopSkill: Boolean
        get() = PlannersOption.root.getBoolean("skill-stop", true)

    @SubscribeEvent
    fun e(e: PlayerDeathEvent) {
        if (stopSkill) {
            e.entity.plannersProfile.runningScripts.values.toList().forEach { script ->
                script.service.terminateQuest(script)
            }
        }
    }

}