package com.bh.planners.core.effect.inline

import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.rootVariables
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import taboolib.module.kether.ScriptContext

class IncidentHitEntity(val owner: Entity, val entity: Entity, val event: Event) : Incident {
    override fun inject(context: ScriptContext) {
        context.rootFrame().rootVariables()["@Event"] = event
        context.rootFrame().rootVariables()["@Target"] = entity.toTarget()
        context.rootFrame().rootVariables()["entity"] = entity
        context.rootFrame().rootVariables()["owner"] = owner
    }
}