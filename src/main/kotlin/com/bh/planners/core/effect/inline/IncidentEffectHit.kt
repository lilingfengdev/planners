package com.bh.planners.core.effect.inline

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.rootVariables
import org.bukkit.entity.Entity
import taboolib.module.kether.ScriptContext

class IncidentEffectHit(val entities : List<Entity>) : Incident{
    override fun inject(context: ScriptContext) {

        val container = Target.Container()
        entities.forEach {
            container += it.toTarget()
        }

        context.rootFrame().rootVariables()["container"] = container
    }
}