package com.bh.planners.core.effect

import com.bh.planners.core.effect.inline.Incident.Companion.handleIncident
import com.bh.planners.core.effect.inline.IncidentEffectHit
import com.bh.planners.core.effect.inline.IncidentEffectTick
import com.bh.planners.core.pojo.Context
import org.bukkit.Location
import org.bukkit.entity.LivingEntity

abstract class EffectICallback<T>(val name: String, val context: Context.SourceImpl) {

    val listeners = mutableMapOf<String, (T) -> Unit>()

    abstract fun onTick(location: Location)

    abstract fun onTick(locations: List<Location>)

    class Tick(name: String, context: Context.SourceImpl) : EffectICallback<MutableList<Location>>(name, context) {

        override fun onTick(location: Location) {
            this.onTick(listOf(location))
        }

        override fun onTick(locations: List<Location>) {

            if (name == "__none__") return

            val mutableList = locations.toMutableList()

            listeners.forEach { it.value(mutableList) }

            val effectTick = IncidentEffectTick(mutableList)
            context.handleIncident(name, effectTick)
        }

    }

    class Hit(name: String, context: Context.SourceImpl) : EffectICallback<MutableList<LivingEntity>>(name, context) {

        override fun onTick(location: Location) {
            this.onTick(listOf(location))
        }

        override fun onTick(locations: List<Location>) {

            if (name == "__none__") return

            locations.forEach {
                it.capture().thenAccept { entities ->
                    if (entities.isNotEmpty()) {
                        context.handleIncident(name, IncidentEffectHit(entities))
                    }
                }
            }

        }

    }

}