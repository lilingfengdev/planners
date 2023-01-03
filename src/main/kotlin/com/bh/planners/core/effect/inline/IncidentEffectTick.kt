package com.bh.planners.core.effect.inline

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.LazyGetter
import com.bh.planners.util.entityAt
import org.bukkit.Location
import taboolib.module.kether.ScriptContext

class IncidentEffectTick(val locations: List<Location>) : Incident {

    override fun inject(context: ScriptContext) {

        // 区别period
        if (locations.size == 1) {
            context.rootFrame().variables()["location"] = locations.first()
        } else {
            context.rootFrame().variables()["locations"] = locations
        }

        context.rootFrame().variables()["targetAt"] = LazyGetter {
            val container = Target.Container()
            container.addAll(locations.flatMap { it.entityAt().map { it.toTarget() } })
            container
        }
    }

}