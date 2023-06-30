package com.bh.planners.core.effect.inline

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.rootVariables
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Projectile
import org.bukkit.event.Event
import taboolib.module.kether.ScriptContext

class IncidentHitBlock(val owner: Entity, val block: Block, val event: Event, val project: Projectile) : Incident {
    override fun inject(context: ScriptContext) {
        val locs = Target.Container()
        val owners = Target.Container()
        val projects = Target.Container()

        locs += block.location.toTarget()
        owners += owner.target()
        projects += project.toTarget()

        context.rootFrame().rootVariables()["@Event"] = event
        context.rootFrame().rootVariables()["owner"] = owners
        context.rootFrame().rootVariables()["loc"] = locs
        context.rootFrame().rootVariables()["location"] = locs
        context.rootFrame().rootVariables()["block"] = block
        context.rootFrame().rootVariables()["project"] = projects
    }
}