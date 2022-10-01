package com.bh.planners.core.effect.inline

import com.bh.planners.core.kether.rootVariables
import org.bukkit.entity.Entity
import taboolib.module.kether.ScriptContext

class IncidentHitEntity(val owner: Entity, val entity: Entity) : Incident{
    override fun inject(context: ScriptContext) {
        context.rootFrame().rootVariables()["entity"] = entity
        context.rootFrame().rootVariables()["owner"] = owner
    }
}