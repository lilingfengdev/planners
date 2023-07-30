package com.bh.planners.core.effect.inline

import com.bh.planners.core.effect.Target
import org.bukkit.entity.Entity
import taboolib.module.kether.ScriptContext

class IncidentGermProjectileHitEntity(val source: Target, val entity: Entity) : Incident {

    override fun inject(context: ScriptContext) {
        context["@source"] = source
        context["@entity"] = entity
    }

}