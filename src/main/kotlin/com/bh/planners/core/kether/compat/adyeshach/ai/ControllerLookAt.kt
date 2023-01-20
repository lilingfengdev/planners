package com.bh.planners.core.kether.compat.adyeshach.ai

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.safeDistance
import ink.ptms.adyeshach.common.entity.ai.Controller
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.potion.PotionEffectType
import taboolib.common.util.random

class ControllerLookAt(val target: Target.Location) : Controller() {

    var distance = 16

    val location : Location
        get() = target.value

    var look = 0

    override fun isAsync(): Boolean {
        return true
    }

    override fun shouldExecute(): Boolean {
        if (entity!!.getTag("isFreeze") == "true" || !entity.isControllerMoving()) {
            if (random(0.01)) {
                look = random(10, 60)
            }
            if (look > 0) {
                look--
                return true
            }
        }
        return false
    }

    override fun onTick() {

        if (entity!!.getLocation().safeDistance(location) < distance) {
            entity.controllerLook(location,smooth = true)
        }

    }
}