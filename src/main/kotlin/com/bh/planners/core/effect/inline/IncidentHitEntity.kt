package com.bh.planners.core.effect.inline

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.rootVariables
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import taboolib.module.kether.ScriptContext

class IncidentHitEntity(val owner: Entity, val entity: Entity, val event: Event) : Incident {
    override fun inject(context: ScriptContext) {
        val entity = Target.Container().add(entity.toTarget())
        val owner = Target.Container().add(owner.target())

        context.rootFrame().rootVariables()["@Event"] = event
        context.rootFrame().rootVariables()["entity"] = entity
        context.rootFrame().rootVariables()["owner"] = owner
    }
}
